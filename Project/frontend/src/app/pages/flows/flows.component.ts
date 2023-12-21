import { Component } from '@angular/core';
import { MatTableModule } from "@angular/material/table";
import {HttpClient} from "@angular/common/http";

export interface FlowElement {
  position: number;
  package: string;
  flowId: number;
  actions: string;
}

const FLOW_DATA: FlowElement[] = [
  { position: 1, package: 'Package 1', flowId: 123, actions: 'Edit, Delete' },
  { position: 2, package: 'Package 2', flowId: 456, actions: 'Edit, Delete' },
  // Add more rows as needed
];

@Component({
  selector: 'app-flows',
  standalone: true,
  imports: [
    MatTableModule
  ],
  templateUrl: './flows.component.html',
  styleUrls: ['./flows.component.scss'] // Fix the typo in styleUrls
})
export class FlowsComponent {
  displayedColumns: string[] = ['position', 'package', 'flowId', 'actions'];
  dataSource = FLOW_DATA;

  constructor(private httpClient: HttpClient) {}

  ngOnInit() {
    this.getPackages();
  }


  getPackages() {
    const apiUrl = 'http://localhost:9001/api/flows/getPackages';

    this.httpClient.get(apiUrl, { responseType: 'json' }).subscribe(
      (response) => {
        console.log('Response:', response);
      },
      (error) => {
        console.error('Error fetching packages:', error);
      }
    );
  }

}
