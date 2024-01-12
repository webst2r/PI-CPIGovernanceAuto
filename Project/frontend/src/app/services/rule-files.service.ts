import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of, tap} from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import {AppConstant} from "../app.constant";
import {GithubRepository} from "../models/repositories";
import {RuleFile} from "../models/rule-file";

@Injectable({
  providedIn: 'root',
})
export class RuleFilesService {

  constructor(private httpClient: HttpClient) {}

  uploadRuleFile(formData: FormData): Observable<any> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.RULEFILES.CREATE;
    const headers = new HttpHeaders();

    return this.httpClient.post(endpoint, formData, { headers }).pipe(
      tap(res => console.log('Successfully created rule file:', res)),
      catchError((error) => {
        console.error('Error uploading rule file:', error);
        return of('Failed to upload rule file');
      })
    );
  }

  checkFileExists(fileName: string): Observable<boolean> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.RULEFILES.CHECK_EXISTENCE + "/" + fileName;

    return this.httpClient.get<boolean>(endpoint).pipe(
      catchError((error) => {
        console.error('Error checking file existence:', error);
        return of(false); // Return false in case of an error
      })
    );
  }

  getAllRuleFiles(): Observable<RuleFile[]> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.RULEFILES.ALL;

    return this.httpClient.get<RuleFile[]>(endpoint).pipe(
      catchError((error) => {
        console.error('Error fetching rule files:', error);
        return of([]);
      })
    );
  }
}
