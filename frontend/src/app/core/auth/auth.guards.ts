import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionStore } from './session-store';

export const authGuard: CanActivateFn = (_route, state) => {
  if (inject(SessionStore).token()) {
    return true;
  }
  return inject(Router).createUrlTree(['/logowanie'], { queryParams: { returnUrl: state.url } });
};

export const moderatorGuard: CanActivateFn = (_route, state) => {
  const session = inject(SessionStore);
  const router = inject(Router);
  if (!session.token()) {
    return router.createUrlTree(['/logowanie'], { queryParams: { returnUrl: state.url } });
  }
  return session.isModerator() || router.createUrlTree(['/']);
};

export const guestGuard: CanActivateFn = () => {
  return !inject(SessionStore).token() || inject(Router).createUrlTree(['/']);
};
