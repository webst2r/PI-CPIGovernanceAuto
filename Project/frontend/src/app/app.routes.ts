import {Routes} from '@angular/router';
import {LoginComponent} from "./pages/login/login.component";
import {RegisterComponent} from "./pages/register/register.component";
import {authGuard} from "./services/auth/auth.guard";
import {HomeComponent} from "./pages/home/home.component";

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
    component:HomeComponent,
    canActivate: [authGuard]
  },
  {
    path: 'home',
    loadComponent: () => import('./pages/home/home.component').then((x)=> x.HomeComponent),
    canActivate: [authGuard],
  },

];

