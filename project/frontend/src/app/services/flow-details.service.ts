import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {AppConstant} from "../app.constant";

@Injectable({
  providedIn: 'root',
})
export class FlowDetailsService {
  constructor(private http: HttpClient) {}

  getFlowDetails(flowId: string, flowVersion: string): Observable<any> {
    const endpoint = AppConstant.API_URL+ AppConstant.API_PATHS.PACKAGES.FLOW_DETAILS + `/${flowId}/${flowVersion}`;
    return this.http.get<any>(endpoint);
  }
}
