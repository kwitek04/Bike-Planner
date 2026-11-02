import { Injectable, signal } from '@angular/core';
import { LatLon } from '../models/geo';

@Injectable({ providedIn: 'root' })
export class MapStore {
  readonly routePoints = signal<LatLon[]>([]);
  readonly routeGeometry = signal<number[][] | null>(null);

  setRoutePoints(points: LatLon[]): void {
    this.routePoints.set(points);
  }

  setRouteGeometry(coordinates: number[][] | null): void {
    this.routeGeometry.set(coordinates);
  }

  clearRoute(): void {
    this.routePoints.set([]);
    this.routeGeometry.set(null);
  }
}
