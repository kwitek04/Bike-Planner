import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Subscription } from 'rxjs';
import { MapStore } from '../../shared/map/map-store';
import { LatLon } from '../../shared/models/geo';
import {
  BaseProfile,
  RouteApi,
  RouteResponse,
  SurfacePreference,
} from './data/route-api';

const MAX_POINTS = 10;

const SURFACE_LABELS: Record<string, string> = {
  asphalt: 'asfalt',
  paved: 'utwardzona',
  concrete: 'beton',
  paving_stones: 'kostka',
  cobblestone: 'bruk',
  compacted: 'ubita',
  fine_gravel: 'drobny żwir',
  gravel: 'żwir',
  unpaved: 'nieutwardzona',
  ground: 'grunt',
  dirt: 'ziemia',
  grass: 'trawa',
  sand: 'piasek',
  wood: 'drewno',
  other: 'inna',
  missing: 'brak danych',
};

@Injectable({ providedIn: 'root' })
export class RoutingFacade {
  private readonly api = inject(RouteApi);
  private readonly mapStore = inject(MapStore);
  private pending?: Subscription;

  readonly points = signal<LatLon[]>([]);
  readonly baseProfile = signal<BaseProfile>('ROAD');
  readonly surface = signal<SurfacePreference>('NO_PREFERENCE');
  readonly avoidMainRoads = signal(false);
  readonly avoidSteepClimbs = signal(false);

  readonly route = signal<RouteResponse | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly geometry = computed(() => this.route()?.geometry.coordinates ?? null);
  readonly maxPoints = MAX_POINTS;

  addPoint(point: LatLon): void {
    if (this.points().length >= MAX_POINTS) {
      return;
    }
    this.points.update((points) => [...points, point]);
    this.syncMapPoints();
    this.plan();
  }

  movePoint(index: number, point: LatLon): void {
    this.points.update((points) => points.map((p, i) => (i === index ? point : p)));
    this.syncMapPoints();
    this.plan();
  }

  removePoint(index: number): void {
    this.points.update((points) => points.filter((_, i) => i !== index));
    this.syncMapPoints();
    this.plan();
  }

  reverse(): void {
    this.points.update((points) => [...points].reverse());
    this.syncMapPoints();
    this.plan();
  }

  clear(): void {
    this.pending?.unsubscribe();
    this.points.set([]);
    this.route.set(null);
    this.error.set(null);
    this.loading.set(false);
    this.mapStore.clearRoute();
  }

  setProfile(value: BaseProfile): void {
    this.baseProfile.set(value);
    this.plan();
  }

  setSurface(value: SurfacePreference): void {
    this.surface.set(value);
    this.plan();
  }

  setAvoidMainRoads(value: boolean): void {
    this.avoidMainRoads.set(value);
    this.plan();
  }

  setAvoidSteepClimbs(value: boolean): void {
    this.avoidSteepClimbs.set(value);
    this.plan();
  }

  pointLabel(index: number): string {
    const count = this.points().length;
    if (index === 0) return 'A';
    if (count > 1 && index === count - 1) return 'B';
    return `${index}`;
  }

  surfaceLabel(surface: string): string {
    return SURFACE_LABELS[surface] ?? surface;
  }

  formatDuration(seconds: number): string {
    const minutes = Math.round(seconds / 60);
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return h > 0 ? `${h} h ${m.toString().padStart(2, '0')} min` : `${m} min`;
  }

  private plan(): void {
    this.pending?.unsubscribe();
    this.error.set(null);
    if (this.points().length < 2) {
      this.route.set(null);
      this.mapStore.setRouteGeometry(null);
      this.loading.set(false);
      return;
    }

    this.loading.set(true);
    this.pending = this.api
      .planRoute({
        points: this.points(),
        baseProfile: this.baseProfile(),
        preferences: {
          surface: this.surface(),
          avoidMainRoads: this.avoidMainRoads(),
          avoidSteepClimbs: this.avoidSteepClimbs(),
        },
      })
      .subscribe({
        next: (route) => {
          this.route.set(route);
          this.mapStore.setRouteGeometry(route.geometry.coordinates);
          this.loading.set(false);
        },
        error: (e: HttpErrorResponse) => {
          this.route.set(null);
          this.mapStore.setRouteGeometry(null);
          this.loading.set(false);
          this.error.set(this.describeError(e));
        },
      });
  }

  private syncMapPoints(): void {
    this.mapStore.setRoutePoints(this.points());
  }

  private describeError(e: HttpErrorResponse): string {
    if (e.status === 0 || e.status >= 500) {
      return 'Brak odpowiedzi serwera.';
    }
    if (e.status === 422) {
      return 'Nie udało się znaleźć trasy między wskazanymi punktami.';
    }
    return e.error?.detail ?? 'Wyznaczenie trasy nie powiodło się.';
  }
}
