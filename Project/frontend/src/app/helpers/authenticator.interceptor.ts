import { HttpInterceptorFn } from '@angular/common/http';
import {inject} from "@angular/core";
import {AuthenticationService} from "../services/authentication.service";

export const authenticatorInterceptor: HttpInterceptorFn = (req, next) => {
  let authReq = req;
  const auth = inject(AuthenticationService);
  const token = auth.getToken();

  // Check if the URL ends with 'authenticate' or 'register'
  if (token && !req.url.endsWith('authenticate') && !req.url.endsWith('register')) {
    // Clone the request and add the authorization header
    authReq = req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + token)
    });
  }

  return next(authReq);
};
