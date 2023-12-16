import { HttpInterceptorFn } from '@angular/common/http';
import {inject} from "@angular/core";
import {AuthenticationService} from "../services/authentication.service";

export const authenticatorInterceptor: HttpInterceptorFn = (req, next) => {
  let authReq = req
  const auth = inject(AuthenticationService)
  const token = auth.getToken();
  if(token!= null){
    authReq = req.clone({headers: req.headers.set('Authorization', 'Bearer ' + token)})
  }
  return next(authReq);
};
