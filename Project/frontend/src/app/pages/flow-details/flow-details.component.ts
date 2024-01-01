import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FlowDetailsService } from '../../services/flow-details.service';

@Component({
  selector: 'app-flow-details',
  standalone: true,
  templateUrl: './flow-details.component.html',
  styleUrls: ['./flow-details.component.scss'],
})
export class FlowDetailsComponent implements OnInit {
  flowDetails: any;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<FlowDetailsComponent>,
    private flowDetailsService: FlowDetailsService
  ) {}

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

    // Make HTTP request to fetch flow details using the FlowDetailsService
    this.flowDetailsService.getFlowDetails(flowId, flowVersion).subscribe(
      (response) => {
        console.log('Flow details response:', response);

        // Access the "_flow" property if it exists
        this.flowDetails = response._flow || response;

        console.log('Flow details: ', this.flowDetails);
      },
      (error) => {
        console.error('Error fetching flow details:', error);
      }
    );
  }
}
