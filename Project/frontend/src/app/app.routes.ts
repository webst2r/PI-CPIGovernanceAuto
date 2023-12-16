import {Routes} from '@angular/router';
import {LoginComponent} from "./pages/login/login.component";
import {RegisterComponent} from "./pages/register/register.component";
import {authGuard} from "./services/auth/auth.guard";
import {HomeComponent} from "./pages/home/home.component";
import {DashboardComponent} from "./pages/dashboard/dashboard.component";

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'home',
    loadComponent: () => import('./pages/home/home.component').then((m)=> m.HomeComponent),
    canActivate: [authGuard],
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard],
  },
];

