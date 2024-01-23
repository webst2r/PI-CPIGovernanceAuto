import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { catchError, Observable, of, tap } from 'rxjs';
import { JenkinsCredentials, JenkinsCredentialsCreateRequest, JenkinsCredentialsUpdateRequest } from '../models/credentials';
import { AppConstant } from '../app.constant';

@Injectable({
  providedIn: 'root'
})
export class JenkinsCredentialsService {
  private http = inject(HttpClient);

  get(): Observable<JenkinsCredentials> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.CREDENTIALS.JENKINS.GET;
    return this.http.get<JenkinsCredentials>(endpoint)
      .pipe(
        catchError(err => {
          console.error('Error getting Jenkins credentials:', err);
          return of();
        })
      );
  }

  create(formData: JenkinsCredentialsCreateRequest): Observable<JenkinsCredentials> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.CREDENTIALS.JENKINS.CREATE;
    return this.http.post<JenkinsCredentials>(endpoint, formData)
      .pipe(
        tap(res => console.log('Successfully created Jenkins credentials:', res)),
        catchError(err => {
          console.error('Error creating Jenkins credentials:', err);
          return of();
        })
      );
  }

  update(formData: JenkinsCredentialsUpdateRequest): Observable<JenkinsCredentials> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.CREDENTIALS.JENKINS.UPDATE;
    return this.http.post<JenkinsCredentials>(endpoint, formData)
      .pipe(
        tap(res => console.log('Successfully updated Jenkins credentials:', res)),
        catchError(err => {
          console.error('Error updating Jenkins credentials:', err);
          return of();
        })
      );
  }

  delete(id: number): Observable<void> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.CREDENTIALS.JENKINS.DELETE;
    const params = new HttpParams()
      .set('id', id.toString());
    return this.http.delete<void>(endpoint, { params })
      .pipe(
        tap(res => console.log('Successfully deleted Jenkins credentials:', res)),
        catchError(err => {
          console.error('Error deleting Jenkins credentials:', err);
          return of();
        })
      );
  }
}
