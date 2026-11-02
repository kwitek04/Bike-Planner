import { Routes } from '@angular/router';
import { AppLayout } from './core/layout/app-layout';
import { PlannerPage } from './features/routing/pages/planner-page';

export const routes: Routes = [
  {
    path: '',
    component: AppLayout,
    children: [{ path: '', component: PlannerPage }],
  },
  { path: '**', redirectTo: '' },
];
