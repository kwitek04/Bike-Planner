import { Injectable, computed, signal } from '@angular/core';
import { Account, AuthResponse } from './auth-api';

const STORAGE_KEY = 'bikeplanner.session';

interface Session {
  token: string;
  expiresAt: string;
  account: Account;
}

@Injectable({ providedIn: 'root' })
export class SessionStore {
  private readonly session = signal<Session | null>(readStoredSession());

  readonly account = computed(() => this.session()?.account ?? null);
  readonly isLoggedIn = computed(() => this.session() !== null);
  readonly isModerator = computed(() => this.account()?.role === 'MODERATOR');

  token(): string | null {
    const session = this.session();
    if (!session) {
      return null;
    }
    if (isExpired(session)) {
      this.clear();
      return null;
    }
    return session.token;
  }

  start(response: AuthResponse): void {
    this.save({ token: response.token, expiresAt: response.expiresAt, account: response.account });
  }

  updateAccount(account: Account): void {
    const session = this.session();
    if (session) {
      this.save({ ...session, account });
    }
  }

  clear(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.session.set(null);
  }

  private save(session: Session): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
    this.session.set(session);
  }
}

function readStoredSession(): Session | null {
  const stored = localStorage.getItem(STORAGE_KEY);
  if (!stored) {
    return null;
  }
  try {
    const session = JSON.parse(stored) as Session;
    return isExpired(session) ? null : session;
  } catch {
    return null;
  }
}

function isExpired(session: Session): boolean {
  return Date.parse(session.expiresAt) <= Date.now();
}
