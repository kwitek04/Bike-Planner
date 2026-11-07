import { Routes } from '@angular/router';
import { guestGuard } from './core/auth/auth.guards';
import { AppLayout } from './core/layout/app-layout';
import { LoginPage } from './features/accounts/pages/login-page';
import { RegisterPage } from './features/accounts/pages/register-page';
import { PlannerPage } from './features/routing/pages/planner-page';

export const routes: Routes = [
  {
    path: '',
    component: AppLayout,
    children: [
      { path: '', component: PlannerPage },
      { path: 'logowanie', component: LoginPage, canActivate: [guestGuard] },
      { path: 'rejestracja', component: RegisterPage, canActivate: [guestGuard] },
    ],
  },
  { path: '**', redirectTo: '' },
];
