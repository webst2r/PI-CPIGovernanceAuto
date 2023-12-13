import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {AuthenticationService} from "../authentication.service";

export const authGuard: CanActivateFn = () => {

  const router = inject(Router);
  const auth = inject(AuthenticationService)
  if (!auth.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }
  router.navigate(['/home'])
  return true;
};
