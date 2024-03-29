import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of, throwError, timeout} from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import {AppConstant} from "../app.constant";
import {PackageDetails} from "../models/package-details";
import {FlowElement} from "../models/flows";
import {ReportDTO} from "../models/report";

@Injectable({
  providedIn: 'root',
})
export class PackageDetailService {
  constructor(private httpClient: HttpClient) {}

  getPackageDetails(packageId: string): Observable<PackageDetails> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.PACKAGES.GET_PACKAGE +`/${packageId}`;

    return this.httpClient.get(endpoint, { responseType: 'json' }).pipe(
      map((response: any) => {
        if (response) {
          return {
            createdBy: response.CreatedBy,
            creationDate: response.CreationDate,
            description: response.Description,
            id: response.Id,
            mode: response.Mode,
            modifiedBy: response.ModifiedBy,
            modifiedDate: response.ModifiedDate,
            name: response.Name,
            partnerContent: response.PartnerContent,
            resourceId: response.ResourceId,
            shortText: response.ShortText,
            supportedPlatform: response.SupportedPlatform,
            updateAvailable: response.UpdateAvailable,
            vendor: response.Vendor,
            version: response.Version,
          };
        } else {
          // Handle the case where response is null
          console.error('Error: Package details response is null.');
          return {
            createdBy: '',
            creationDate: '',
            description: '',
            id: '',
            mode: '',
            modifiedBy: '',
            modifiedDate: '',
            name: '',
            partnerContent: false,
            resourceId: '',
            shortText: '',
            supportedPlatform: '',
            updateAvailable: false,
            vendor: '',
            version: '',
          };
        }
      }),
      catchError((error) => {
        console.error('Error fetching package details:', error);
        return of({
          createdBy: '',
          creationDate: '',
          description: '',
          id: '',
          mode: '',
          modifiedBy: '',
          modifiedDate: '',
          name: '',
          partnerContent: false,
          resourceId: '',
          shortText: '',
          supportedPlatform: '',
          updateAvailable: false,
          vendor: '',
          version: '',
        });
      })
    );
  }


  getPackageFlows(packageId: string): Observable<FlowElement[]> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.PACKAGES.GET_PACKAGE_FLOWS +`/${packageId}`;

    return this.httpClient.get(endpoint, { responseType: 'json' }).pipe(
      map((response: any) =>
        response.results.map((result: any, index: number) => ({
          id: result.Id,
          position: index + 1,
          name: result.Name,
          version: result.Version,
          modifiedBy: result.ModifiedBy,
          modifiedDate: result.ModifiedDate,
        }))
      ),
      catchError((error) => {
        console.error('Error fetching package flows:', error);
        return of([]);
      })
    );
  }

  downloadFlow(flowId: string, flowVersion: string): Observable<Blob> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.PACKAGES.DOWNLOAD_FLOW + `/${flowId}/${flowVersion}`;

    // Set headers to accept 'application/zip' content type
    const headers = new HttpHeaders({
      'Accept': 'application/zip',
    });

    return this.httpClient.get(endpoint, { headers, responseType: 'blob' });
  }

  enableJenkins(jobName: string, ruleFileName: string, codenarcFileName: string, flowVersion: string): Observable<ReportDTO> {
    const endpoint = `${AppConstant.API_URL}${AppConstant.API_PATHS.PACKAGES.CREATE_EXECUTE_PIPELINE}/${jobName}/${ruleFileName}/${codenarcFileName}/${flowVersion}`;

    return this.httpClient.get<ReportDTO>(endpoint).pipe(
      timeout(180000),
      map((response: ReportDTO) => response),
      catchError((error) => {
        console.error('Error enabling Jenkins for the flow:', error);
        return throwError('Failed to enable Jenkins for the flow');
      })
    );
  }

  enableGithub(flowId: string, flowVersion: string, branch: string): Observable<string> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.PACKAGES.ENABLE_GITHUB + `/${flowId}/${flowVersion}/${branch}`;

    return this.httpClient.get(endpoint, { responseType: 'text' }).pipe(
      map((response: any) => response),
      catchError((error) => {
        console.error('Error enabling Github for the flow:', error);
        return of('Failed to enable Github for the flow');
      })
    );
  }

  uploadFlowZip(flowName: string, zipFile: Blob): Observable<void> {
    const endpoint = AppConstant.API_URL + AppConstant.API_PATHS.PACKAGES.UPLOAD_FLOW_ZIP;
    flowName += '.zip';
    // Create a FormData object
    const formData = new FormData();
    formData.append('zipFile', zipFile, flowName);

    return this.httpClient.post<void>(endpoint, formData);
  }

}
