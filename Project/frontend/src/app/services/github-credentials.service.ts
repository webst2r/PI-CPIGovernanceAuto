import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, Observable, of, tap} from "rxjs";
import {GithubCredentials, GithubCredentialsCreateRequest, GithubCredentialsUpdateRequest} from "../models/credentials";
import {AppConstant} from "../app.constant";

@Injectable({
  providedIn: 'root'
})
export class GithubCredentialsService {
  private http = inject(HttpClient)

  get(): Observable<GithubCredentials> {
    const endpoint = AppConstant.API_URL+ AppConstant.API_PATHS.CREDENTIALS.GITHUB.GET;
    return this.http.get<GithubCredentials>(endpoint)
      .pipe(
        catchError(err => {
          console.error('Error getting sap-cpi credentials:', err);
          return of()
        })
      );
  }

  create(formData: GithubCredentialsCreateRequest): Observable<GithubCredentials> {
    const endpoint = AppConstant.API_URL+ AppConstant.API_PATHS.CREDENTIALS.GITHUB.CREATE;
    return this.http.post<GithubCredentials>(endpoint, formData)
      .pipe(
        tap(res => console.log('Successfully created credentials:', res)),
        catchError(err => {
          console.error('Error creating credentials:', err);
          return of()
        })
      );
  }

  update(formData: GithubCredentialsUpdateRequest): Observable<GithubCredentials> {
    const endpoint = AppConstant.API_URL+ AppConstant.API_PATHS.CREDENTIALS.GITHUB.UPDATE;
    return this.http.post<GithubCredentials>(endpoint, formData)
      .pipe(
        tap(res => console.log('Successfully updated credentials:', res)),
        catchError(err => {
          console.error('Error updating credentials:', err);
          return of()
        })
      );
  }

  delete(id: number): Observable<void> {
    const endpoint =AppConstant.API_URL+ AppConstant.API_PATHS.CREDENTIALS.GITHUB.DELETE;
    const params = new HttpParams()
      .set('id', id);
    return this.http.delete<void>(endpoint, {params})
      .pipe(
        tap(res => console.log('Successfully deleted credentials:', res)),
        catchError(err => {
          console.error('Error creating credentials:', err);
          return of()
        })
      );
  }
}
