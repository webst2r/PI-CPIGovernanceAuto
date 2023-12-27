import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {AuthenticationService} from "../authentication.service";

export const authGuard: CanActivateFn = () => {

  const router = inject(Router);
  const auth = inject(AuthenticationService)
  return auth.isAuthenticated();
};
