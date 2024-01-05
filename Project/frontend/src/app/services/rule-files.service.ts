import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class RuleFilesService {
  private apiUrl = 'http://localhost:9001/api/packages';

  constructor(private httpClient: HttpClient) {}

  uploadRuleFile(formData: FormData): Observable<any> {
    const uploadUrl = `${this.apiUrl}/uploadRuleFile`;
    const headers = new HttpHeaders();

    return this.httpClient.post(uploadUrl, formData, { headers }).pipe(
      catchError((error) => {
        console.error('Error uploading rule file:', error);
        return of('Failed to upload rule file');
      })
    );
  }
}
