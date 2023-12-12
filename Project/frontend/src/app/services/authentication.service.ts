import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthenticationResponse} from "../models/authentication-response";
import {RegisterRequest} from "../models/register-request";
import {AuthenticationRequest} from "../models/authentication-request";
import {AppConstant} from "../app.constant";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  constructor( private http: HttpClient) { }

  register(
    registerRequest: RegisterRequest
  ) {
    return this.http.post<AuthenticationResponse>
    (AppConstant.API_URL+AppConstant.API_PATHS.AUTH.REGISTER, registerRequest);
  }

  login(
    authRequest: AuthenticationRequest
  ) {
    return this.http.post<AuthenticationResponse>
    (AppConstant.API_URL+ AppConstant.API_PATHS.AUTH.LOGIN, authRequest);
  }
}
