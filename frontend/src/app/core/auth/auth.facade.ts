import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthApi, AuthResponse, Credentials } from './auth-api';
import { SessionStore } from './session-store';

@Injectable({ providedIn: 'root' })
export class AuthFacade {
  private readonly api = inject(AuthApi);
  private readonly session = inject(SessionStore);
  private readonly router = inject(Router);

  readonly account = this.session.account;
  readonly isLoggedIn = this.session.isLoggedIn;
  readonly isModerator = this.session.isModerator;

  readonly pending = signal(false);
  readonly error = signal<string | null>(null);

  login(credentials: Credentials, returnUrl = '/'): void {
    this.submit(this.api.login(credentials), returnUrl);
  }

  register(credentials: Credentials): void {
    this.submit(this.api.register(credentials), '/');
  }

  logout(): void {
    this.session.clear();
    void this.router.navigateByUrl('/');
  }

  restoreSession(): void {
    if (this.session.token()) {
      this.api.me().subscribe({
        next: (account) => this.session.updateAccount(account),
        error: () => {},
      });
    }
  }

  resetError(): void {
    this.error.set(null);
  }

  private submit(request: Observable<AuthResponse>, returnUrl: string): void {
    this.pending.set(true);
    this.error.set(null);
    request.subscribe({
      next: (response) => {
        this.session.start(response);
        this.pending.set(false);
        void this.router.navigateByUrl(returnUrl);
      },
      error: (e: HttpErrorResponse) => {
        this.pending.set(false);
        this.error.set(this.describeError(e));
      },
    });
  }

  private describeError(e: HttpErrorResponse): string {
    if (e.status === 0 || e.status >= 500) {
      return 'Brak odpowiedzi serwera.';
    }
    if (e.status === 401) {
      return 'Nieprawidłowy e-mail lub hasło.';
    }
    if (e.status === 409) {
      return 'Konto z tym adresem e-mail już istnieje.';
    }
    return 'Sprawdź poprawność wpisanych danych.';
  }
}
