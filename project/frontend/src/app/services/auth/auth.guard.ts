import {Router, UrlTree} from '@angular/router';
import {inject} from "@angular/core";
import {AuthenticationService} from "../authentication.service";
import {Observable} from "rxjs";

export const authGuard = (): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree => {

  const router = inject(Router);
  const auth = inject(AuthenticationService)

  if (auth.isAuthenticated()) {
    return true;
  }

  return router.parseUrl('/login');
};
