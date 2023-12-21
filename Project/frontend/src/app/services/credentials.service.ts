import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CredentialsService {
  private apiUrl = 'http://localhost:9001/api/credentials';

  constructor(private http: HttpClient) {}

  registerCredentials(formData: any): Observable<any> {
    const endpoint = `${this.apiUrl}/registerCredentials`;
    return this.http.post(endpoint, formData);
  }
}
