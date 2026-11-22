import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { SessionStore } from '../../core/auth/session-store';
import { RouteRequest } from '../routing/data/route-api';
import { RoutingFacade } from '../routing/routing.facade';
import { FavoriteApi, FavoriteRoute } from './data/favorite-api';

@Injectable({ providedIn: 'root' })
export class FavoritesFacade {
  private readonly api = inject(FavoriteApi);
  private readonly routing = inject(RoutingFacade);
  private readonly session = inject(SessionStore);
  private readonly router = inject(Router);

  readonly favorites = signal<FavoriteRoute[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly saving = signal(false);
  readonly saveError = signal<string | null>(null);
  private readonly lastSaved = signal<{ request: RouteRequest; name: string } | null>(null);

  readonly isLoggedIn = this.session.isLoggedIn;
  readonly hasRoute = computed(() => this.routing.route() !== null && !this.routing.loading());
  readonly savedAs = computed(() => {
    const saved = this.lastSaved();
    return saved && saved.request === this.routing.request() ? saved.name : null;
  });

  load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.api.list().subscribe({
      next: (favorites) => {
        this.favorites.set(favorites);
        this.loading.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(this.describeError(e, 'Nie udało się pobrać ulubionych tras.'));
      },
    });
  }

  save(name: string): void {
    const request = this.routing.request();
    if (!request || !this.hasRoute()) {
      return;
    }
    this.saving.set(true);
    this.saveError.set(null);
    this.api.save({ ...request, name: name.trim() }).subscribe({
      next: (favorite) => {
        this.favorites.update((favorites) => [favorite, ...favorites]);
        this.lastSaved.set({ request, name: favorite.name });
        this.saving.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.saving.set(false);
        this.saveError.set(this.describeError(e, 'Nie udało się zapisać trasy.'));
      },
    });
  }

  remove(id: number): void {
    this.error.set(null);
    this.api.delete(id).subscribe({
      next: () => this.dropFromList(id),
      error: (e: HttpErrorResponse) => {
        if (e.status === 404) {
          this.dropFromList(id);
          return;
        }
        this.error.set(this.describeError(e, 'Nie udało się usunąć trasy.'));
      },
    });
  }

  open(favorite: FavoriteRoute): void {
    this.routing.load(favorite.route);
    void this.router.navigateByUrl('/');
  }

  resetSaveError(): void {
    this.saveError.set(null);
  }

  private dropFromList(id: number): void {
    this.favorites.update((favorites) => favorites.filter((f) => f.id !== id));
  }

  private describeError(e: HttpErrorResponse, fallback: string): string {
    if (e.status === 0 || e.status >= 500) {
      return 'Brak odpowiedzi serwera.';
    }
    if (e.status === 401) {
      return 'Sesja wygasła. Zaloguj się ponownie.';
    }
    if (e.status === 422) {
      return 'Nie udało się wyznaczyć trasy między wskazanymi punktami.';
    }
    if (e.status === 400) {
      return 'Sprawdź poprawność nazwy i punktów trasy.';
    }
    return fallback;
  }
}
