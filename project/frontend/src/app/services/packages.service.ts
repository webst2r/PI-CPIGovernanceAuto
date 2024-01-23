import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import {AppConstant} from "../app.constant";
import {PackagesElement} from "../models/packages";

@Injectable({
  providedIn: 'root',
})
export class PackagesService {

  constructor(private httpClient: HttpClient) {
  }

  getPackages(): Observable<PackagesElement[]> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.PACKAGES.GET_PACKAGES;

    return this.httpClient.get(endpoint, {responseType: 'json'}).pipe(
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
