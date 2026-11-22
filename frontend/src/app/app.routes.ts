import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './core/auth/auth.guards';
import { AppLayout } from './core/layout/app-layout';
import { LoginPage } from './features/accounts/pages/login-page';
import { RegisterPage } from './features/accounts/pages/register-page';
import { FavoritesPage } from './features/preferences/pages/favorites-page';
import { PlannerPage } from './features/routing/pages/planner-page';

export const routes: Routes = [
  {
    path: '',
    component: AppLayout,
    children: [
      { path: '', component: PlannerPage },
      { path: 'logowanie', component: LoginPage, canActivate: [guestGuard] },
      { path: 'rejestracja', component: RegisterPage, canActivate: [guestGuard] },
      { path: 'ulubione', component: FavoritesPage, canActivate: [authGuard] },
    ],
  },
  { path: '**', redirectTo: '' },
];
