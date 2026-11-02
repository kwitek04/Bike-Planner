import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { LatLon } from '../../../shared/models/geo';

export type BaseProfile = 'ROAD' | 'GRAVEL' | 'MTB';
export type SurfacePreference = 'NO_PREFERENCE' | 'PREFER_PAVED' | 'PREFER_UNPAVED';

export interface RouteRequest {
  points: LatLon[];
  baseProfile: BaseProfile;
  preferences: {
    surface: SurfacePreference;
    avoidMainRoads: boolean;
    avoidSteepClimbs: boolean;
  };
}

export interface SurfaceShare {
  surface: string;
  distanceMeters: number;
  share: number;
}

export interface RouteResponse {
  geometry: {
    type: 'LineString';
    coordinates: number[][];
  };
  distanceMeters: number;
  durationSeconds: number;
  ascentMeters: number;
  descentMeters: number;
  surfaceShares: SurfaceShare[];
}

@Injectable({ providedIn: 'root' })
export class RouteApi {
  private readonly http = inject(HttpClient);

  planRoute(request: RouteRequest): Observable<RouteResponse> {
    return this.http.post<RouteResponse>('/api/v1/routes', request);
  }
}
