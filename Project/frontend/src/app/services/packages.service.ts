import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class PackagesService {
  private apiUrl = 'http://localhost:9001/api/packages';

  constructor(private httpClient: HttpClient) {}

  getPackages(): Observable<PackagesElement[]> {
    const apiUrl = `${this.apiUrl}/getPackages`;

    return this.httpClient.get(apiUrl, { responseType: 'json' }).pipe(
      map((response: any) =>
        response.results.map((result: any, index: number) => ({
          position: index + 1,
          name: result.Name,
          version: result.Version,
          modifiedBy: result.ModifiedBy,
          modifiedDate: result.ModifiedDate,
        }))
      ),
      catchError((error) => {
        console.error('Error fetching packages:', error);
        return of([]);
      })
    );
  }
}

export interface PackagesElement {
  position: number;
  name: string;
  version: string;
  modifiedBy: string;
  modifiedDate: string;
}
