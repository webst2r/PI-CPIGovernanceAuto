import { Component } from '@angular/core';
import { MatTableModule } from "@angular/material/table";

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
}
