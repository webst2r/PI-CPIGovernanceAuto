import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {catchError, Observable, of, tap} from 'rxjs';
import {SapCpiCredentials, SapCpiCredentialsCreateRequest, SapCpiCredentialsUpdateRequest} from "../models/credentials";
import {AppConstant} from "../app.constant";

@Injectable({
  providedIn: 'root',
})
export class SapCpiCredentialsService {
  private http = inject(HttpClient)
  get(): Observable<SapCpiCredentials> {
    const endpoint = AppConstant.API_URL+ AppConstant.API_PATHS.CREDENTIALS.SAP_CPI.GET;
    return this.http.get<SapCpiCredentials>(endpoint)
      .pipe(
        catchError(err => {
          console.error('Error getting sap-cpi credentials:', err);
          return of()
        })
      );
  }

  create(formData: SapCpiCredentialsCreateRequest): Observable<SapCpiCredentials> {
    const endpoint = AppConstant.API_URL+ AppConstant.API_PATHS.CREDENTIALS.SAP_CPI.CREATE;
    return this.http.post<SapCpiCredentials>(endpoint, formData)
      .pipe(
        tap(res => console.log('Successfully created credentials:', res)),
        catchError(err => {
          console.error('Error creating credentials:', err);
          return of()
        })
      );
  }

  update(formData: SapCpiCredentialsUpdateRequest): Observable<SapCpiCredentials> {
    const endpoint = AppConstant.API_URL+ AppConstant.API_PATHS.CREDENTIALS.SAP_CPI.UPDATE;
    return this.http.post<SapCpiCredentials>(endpoint, formData)
      .pipe(
        tap(res => console.log('Successfully updated credentials:', res)),
        catchError(err => {
          console.error('Error updating credentials:', err);
          return of()
        })
      );
  }

  delete(id: number): Observable<void> {
    const endpoint = AppConstant.API_URL+ AppConstant.API_PATHS.CREDENTIALS.SAP_CPI.DELETE;
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
