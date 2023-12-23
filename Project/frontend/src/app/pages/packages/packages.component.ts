import { Component } from '@angular/core';
import { MatTableModule } from "@angular/material/table";
import { HttpClient } from "@angular/common/http";
import { Router } from '@angular/router';
import {MatIconModule} from "@angular/material/icon";

export interface PackagesElement {
  position: number;
  name: string;
  version: string;
  modifiedBy: string;
  modifiedDate: string;
}

@Component({
  selector: 'app-packages',
  standalone: true,
  imports: [
    MatTableModule,
    MatIconModule
  ],
  templateUrl: './packages.component.html',
  styleUrls: ['./packages.component.scss']
})
export class PackagesComponent {
  displayedColumns: string[] = ['position', 'name', 'version', 'modifiedBy', 'modifiedDate', 'actions'];
  dataSource: PackagesElement[] = [];

  constructor(private httpClient: HttpClient, private router: Router) {}

  ngOnInit() {
    this.getPackages();
  }

  getPackages() {
    const apiUrl = 'http://localhost:9001/api/packages/getPackages';

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

  openPackage(element: PackagesElement) {
    // Navigate to the package detail view or perform any action you want when a row is clicked
    this.router.navigate(['/package-detail', element.name]); // Update with your actual detail route
  }
}
