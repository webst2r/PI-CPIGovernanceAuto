import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatTableModule } from "@angular/material/table";
import { HttpClient } from "@angular/common/http";

@Component({
  selector: 'app-flow-details',
  standalone: true,
  imports: [
    MatPaginatorModule,
    MatTableModule
  ],
  templateUrl: './flow-details.component.html',
  styleUrls: ['./flow-details.component.scss']
})
export class FlowDetailsComponent {
  flowDetails: any;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<FlowDetailsComponent>,
    private http: HttpClient
  ) { }

  ngOnInit() {
    this.dialogRef.updateSize('80%', '80%');

    // Check if data is available
    if (this.data && this.data.name) {
      this.fetchFlowDetails();
    }
  }

  closeDialog() {
    this.dialogRef.close();
  }

  private fetchFlowDetails() {
    const flowId = this.data.name;
    const flowVersion = this.data.version;

    console.log('Fetching flow details for:', flowId, flowVersion);

    // Make HTTP request to fetch flow details using the correct flow ID and version
    this.http.get<any>(`http://localhost:9001/api/packages/getFlow/${flowId}/${flowVersion}`)
      .subscribe(
        response => {
          console.log('Flow details response:', response);

          // Access the "_flow" property if it exists
          this.flowDetails = response._flow || response;

          // Move the console.log here
          console.log("Flow details: ", this.flowDetails);
        },
        error => {
          console.error('Error fetching flow details:', error);
        }
      );
  }
}
