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
    this.dialogRef.updateSize('50%', '50%');

    // Check if data is available
    if (this.data && this.data.id) {
      this.fetchFlowDetails();
    }
  }

  closeDialog() {
    this.dialogRef.close();
  }

  private fetchFlowDetails() {
    const flowId = this.data.id;
    const flowVersion = this.data.version;

    console.log('Flow details data:', this.data);

    console.log('Fetching flow details for:', flowId, flowVersion);

    this.flowDetailsService.getFlowDetails(flowId, flowVersion).subscribe(
      (response) => {
        console.log('Flow details response:', response);

        this.flowDetails = response._flow || response;

        console.log('Flow details: ', this.flowDetails);
      },
      (error) => {
        console.error('Error fetching flow details:', error);
      }
    );
  }
}
