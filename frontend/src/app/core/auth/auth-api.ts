import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export type Role = 'CYCLIST' | 'MODERATOR';

export interface Account {
  id: number;
  email: string;
  role: Role;
}

export interface Credentials {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  expiresAt: string;
  account: Account;
}

@Injectable({ providedIn: 'root' })
export class AuthApi {
  private readonly http = inject(HttpClient);

  login(credentials: Credentials): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/v1/auth/login', credentials);
  }

  register(credentials: Credentials): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/v1/auth/register', credentials);
  }

  me(): Observable<Account> {
    return this.http.get<Account>('/api/v1/auth/me');
  }
}
