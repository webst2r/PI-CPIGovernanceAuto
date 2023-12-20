import { Routes } from '@angular/router';
import { LoginComponent } from "./pages/login/login.component";
import { RegisterComponent } from "./pages/register/register.component";
import { HomeComponent } from "./pages/home/home.component";  // Import HomeComponent
import { authGuard } from "./services/auth/auth.guard";
import {FlowsComponent} from "./pages/flows/flows.component";
import {CredentialsComponent} from "./pages/credentials/credentials.component";

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
    path: 'flows',
    component: FlowsComponent,
    canActivate: [authGuard]
  },
  {
    path: 'credentials',
    component: CredentialsComponent,
    canActivate: [authGuard]
  },
];
