import { Routes } from '@angular/router';
import { LoginComponent } from "./pages/login/login.component";
import { RegisterComponent } from "./pages/register/register.component";
import { HomeComponent } from "./pages/home/home.component";  // Import HomeComponent
import { authGuard } from "./services/auth/auth.guard";
import {PackagesComponent} from "./pages/packages/packages.component";
import {CredentialsComponent} from "./pages/credentials/credentials.component";
import {PackageDetailComponent} from "./pages/package-detail/package-detail.component";
import {publicGuard} from "./services/auth/public.guard";
import {LogoutComponent} from "./pages/logout/logout.component";
import {RepositoriesComponent} from "./pages/repositories/repositories.component";
import {RulesComponent} from "./pages/rules/rules.component";
import {JenkinsReportComponent} from "./pages/jenkins-report/jenkins-report.component";

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [publicGuard]
  },
  {
    path: 'register',
    component: RegisterComponent,
    canActivate: [publicGuard]
  },
  {
    path: 'logout',
    component: LogoutComponent,
    canActivate: [authGuard]
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
  {
    path: 'repositories',
    component: RepositoriesComponent,
    canActivate: [authGuard]
  },
  {
    path: 'rules',
    component: RulesComponent,
    canActivate: [authGuard]
  },
  { path: 'package-detail/:id',
    component: PackageDetailComponent,
    canActivate: [authGuard]
  },
  { path: 'packages/jenkins/report',
    component: JenkinsReportComponent,
    canActivate: [authGuard]
  }
];
