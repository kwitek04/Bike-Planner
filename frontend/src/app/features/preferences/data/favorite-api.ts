import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { RouteRequest } from '../../routing/data/route-api';

export interface SaveFavoriteRouteRequest extends RouteRequest {
  name: string;
}

export interface FavoriteRoute {
  id: number;
  name: string;
  route: RouteRequest;
  distanceMeters: number;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class FavoriteApi {
  private readonly http = inject(HttpClient);

  list(): Observable<FavoriteRoute[]> {
    return this.http.get<FavoriteRoute[]>('/api/v1/favorites');
  }

  save(request: SaveFavoriteRouteRequest): Observable<FavoriteRoute> {
    return this.http.post<FavoriteRoute>('/api/v1/favorites', request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`/api/v1/favorites/${id}`);
  }
}
