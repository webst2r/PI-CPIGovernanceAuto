import { Component } from '@angular/core';
import { MatTableModule } from "@angular/material/table";
import { HttpClient } from "@angular/common/http";

export interface FlowElement {
  position: number;
  name: string;
  version: string;
  modifiedBy: string;
  modifiedDate: string;
}

@Component({
  selector: 'app-flows',
  standalone: true,
  imports: [
    MatTableModule
  ],
  templateUrl: './packages.component.html',
  styleUrls: ['./packages.component.scss']
})
export class PackagesComponent {
  displayedColumns: string[] = ['position', 'name', 'version', 'modifiedBy', 'modifiedDate'];
  dataSource: FlowElement[] = [];

  constructor(private httpClient: HttpClient) {}

  ngOnInit() {
    this.getPackages();
  }

  getPackages() {
    const apiUrl = 'http://localhost:9001/api/flows/getPackages';

    this.httpClient.get(apiUrl, { responseType: 'json' }).subscribe(
      (response: any) => {
        // Assuming the response has a 'results' property which is an array
        this.dataSource = response.results.map((result: any, index: number) => ({
          position: index + 1,
          name: result.Name,
          version: result.Version,
          modifiedBy: result.ModifiedBy,
          modifiedDate: result.ModifiedDate
        }));
        console.log(response);
      },
      (error) => {
        console.error('Error fetching packages:', error);
      }
    );
  }
}
