import { Routes } from '@angular/router';
import { LoginComponent } from "./pages/login/login.component";
import { RegisterComponent } from "./pages/register/register.component";
import { HomeComponent } from "./pages/home/home.component";  // Import HomeComponent
import { authGuard } from "./services/auth/auth.guard";
import {PackagesComponent} from "./pages/packages/packages.component";
import {CredentialsComponent} from "./pages/credentials/credentials.component";
import {PackageDetailComponent} from "./pages/package-detail/package-detail.component";

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
    component: HomeComponent,
    canActivate: [authGuard]
  },
  {
    path: 'packages',
    component: PackagesComponent,
    canActivate: [authGuard]
  },
  {
    path: 'credentials',
    component: CredentialsComponent,
    canActivate: [authGuard]
  },
  { path: 'package-detail/:id',
    component: PackageDetailComponent,
    canActivate: [authGuard]
  }
];
