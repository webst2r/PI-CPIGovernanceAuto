import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, Observable, of} from "rxjs";
import {AppConstant} from "../app.constant";
import {ReportDTO} from "../models/report";

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private http = inject(HttpClient)

  get(): Observable<ReportDTO> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.REPORT.GET;
    return this.http.get<ReportDTO>(endpoint)
      .pipe(
        catchError(err => {
          console.error('Error getting jenkins report:', err);
          return of()
        })
      );
  }
}
