import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {forkJoin, Observable, of, tap} from 'rxjs';
import { catchError } from 'rxjs/operators';
import {AppConstant} from "../app.constant";
import {RuleFile} from "../models/rule-file";

@Injectable({
  providedIn: 'root',
})
export class RuleFilesService {

  constructor(private httpClient: HttpClient) {
  }

  uploadRuleFile(formData: FormData): Observable<any> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.RULEFILES.CPI.CREATE;
    const headers = new HttpHeaders();

    return this.httpClient.post(endpoint, formData, {headers}).pipe(
      tap(res => console.log('Successfully created rule file:', res)),
      catchError((error) => {
        console.error('Error uploading rule file:', error);
        return of('Failed to upload rule file');
      })
    );
  }

  checkFileExists(fileName: string): Observable<boolean> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.RULEFILES.CPI.CHECK_EXISTENCE + "/" + fileName;

    return this.httpClient.get<boolean>(endpoint).pipe(
      catchError((error) => {
        console.error('Error checking file existence:', error);
        return of(false); // Return false in case of an error
      })
    );
  }

  getAllRuleFiles(): Observable<RuleFile[]> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.RULEFILES.CPI.ALL;

    return this.httpClient.get<RuleFile[]>(endpoint).pipe(
      catchError((error) => {
        console.error('Error fetching rule files:', error);
        return of([]);
      })
    );
  }

  getAllCodenarcFiles(): Observable<RuleFile[]> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.RULEFILES.CODENARC.ALL;

    return this.httpClient.get<RuleFile[]>(endpoint).pipe(
      catchError((error) => {
        console.error('Error fetching codenarc files:', error);
        return of([]);
      })
    );
  }

  getAllRuleAndCodenarcFiles(): Observable<{ ruleFiles: RuleFile[], codenarcFiles: RuleFile[] }> {
    return forkJoin({
      ruleFiles: this.getAllRuleFiles(),
      codenarcFiles: this.getAllCodenarcFiles()
    });
  }

  deleteCodenarcFile(id: string): Observable<any> {
    const endpoint = `${AppConstant.API_URL}${AppConstant.API_PATHS.RULEFILES.CODENARC.DELETE}/${id}`;

    return this.httpClient.delete(endpoint).pipe(
      tap(() => console.log('Successfully deleted Codenarc file')),
      catchError((error) => {
        console.error('Error deleting Codenarc file:', error);
        return of('Failed to delete Codenarc file');
      })
    );
  }

  deleteRuleFile(id: string): Observable<any> {
    const endpoint = `${AppConstant.API_URL}${AppConstant.API_PATHS.RULEFILES.CPI.DELETE}/${id}`;

    return this.httpClient.delete(endpoint).pipe(
      tap(() => console.log('Successfully deleted Rule file')),
      catchError((error) => {
        console.error('Error deleting Rule file:', error);
        return of('Failed to delete Rule file');
      })
    );

  }
}
