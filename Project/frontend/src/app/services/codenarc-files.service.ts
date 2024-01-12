import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of, tap} from 'rxjs';
import { catchError } from 'rxjs/operators';
import {AppConstant} from "../app.constant";
import {CodenarcFile} from "../models/rule-file";

@Injectable({
  providedIn: 'root',
})

export class CodenarcFilesService {

  constructor(private httpClient: HttpClient) {}

  uploadCodenarcFile(formData: FormData): Observable<any> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.RULEFILES.CODENARC.CREATE;
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
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.RULEFILES.CODENARC.CHECK_EXISTENCE + "/" + fileName;

    return this.httpClient.get<boolean>(endpoint).pipe(
      catchError((error) => {
        console.error('Error checking file existence:', error);
        return of(false);
      })
    );
  }

  getAllCodenarcFiles(): Observable<CodenarcFile[]> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.RULEFILES.CODENARC.ALL;

    return this.httpClient.get<CodenarcFile[]>(endpoint).pipe(
      catchError((error) => {
        console.error('Error fetching rule files:', error);
        return of([]);
      })
    );
  }
}
