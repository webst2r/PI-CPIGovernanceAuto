import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthenticationResponse} from "../models/authentication-response";
import {RegisterRequest} from "../models/register-request";
import {AuthenticationRequest} from "../models/authentication-request";
import {AppConstant} from "../app.constant";
import {StorageKey, StorageService} from "./storage.service";
import {JwtHelperService} from "@auth0/angular-jwt";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  constructor( private http: HttpClient,
               private readonly storageService: StorageService,
               private jwtHelperService:  JwtHelperService) { }

  register(
    registerRequest: RegisterRequest
  ) {
    return this.http.post<String>
    (AppConstant.API_URL+AppConstant.API_PATHS.AUTH.REGISTER, registerRequest);
  }

  login(
    authRequest: AuthenticationRequest
  ) {
    return this.http.post<AuthenticationResponse>
    (AppConstant.API_URL+ AppConstant.API_PATHS.AUTH.LOGIN, authRequest);
  }

  getToken(): string | null{
    return this.storageService.getData(StorageKey.TOKEN);
  }

  saveToken(token: string){
    this.storageService.saveData(StorageKey.TOKEN, token);
  }

  saveUser(user: AuthenticationResponse){
    this.storageService.saveData(StorageKey.USER, JSON.stringify(user));
  }

  getUser(): AuthenticationResponse | null {
    const user = this.storageService.getData(StorageKey.USER);
    if (user) {
      return JSON.parse(user);
    }
    return null;
  }

  isAuthenticated(): boolean {
    const token = this.storageService.getData(StorageKey.TOKEN);
    // Check whether the token is expired and return
    // true or false
    if(token !== null) {
      return !this.jwtHelperService.isTokenExpired(token);
    }
    return false;
  }
}
