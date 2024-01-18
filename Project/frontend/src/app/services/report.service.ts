import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, catchError, Observable, of, Subject} from "rxjs";
import {AppConstant} from "../app.constant";
import {ReportDTO} from "../models/report";

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private dataSubject: BehaviorSubject<ReportDTO> = new BehaviorSubject<any>(null);
  public data$: Observable<ReportDTO> = this.dataSubject.asObservable();

  set(data: any): void {
    this.dataSubject.next(data);
  }

  get(): Observable<any> {
    return this.data$;
  }
}
