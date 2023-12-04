import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {AppConstant} from "../app.constant";
import {StorageKey, StorageService} from "./storage.service";
import {UserToken} from "../interfaces/user";
import {JwtHelperService} from "@auth0/angular-jwt";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(private http: HttpClient,
              private readonly storageService: StorageService,
               private jwtHelperService:  JwtHelperService) { }

  login(email: string, password: string): Observable<UserToken>{
    return this.http.post(AppConstant.API_URL+ AppConstant.API_PATHS.USER.LOGIN, {
      email, password
    }) as Observable<UserToken>;
  }

  register(email: string, password: string, firstName: string, lastName: string): Observable<any>{
    return this.http.post(AppConstant.API_URL+ AppConstant.API_PATHS.USER.REGISTER, {
      email, password, firstName, lastName
    }) as Observable<any>;
  }

  logout(){
    this.storageService.clearData();
  }

  saveToken(token: string){
    this.storageService.saveData(StorageKey.TOKEN, token);
  }

  getToken(): string | null{
    return this.storageService.getData(StorageKey.TOKEN);
  }

  saveUser(user: UserToken){
    this.storageService.saveData(StorageKey.USER, JSON.stringify(user));
  }

  getUser(): UserToken | null {
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
