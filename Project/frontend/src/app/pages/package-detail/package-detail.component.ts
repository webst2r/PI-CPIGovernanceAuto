import {Component, OnInit, ViewChild} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {MatIconModule} from "@angular/material/icon";
import { Router } from '@angular/router';
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {MatDialog} from "@angular/material/dialog";
import {FlowDetailsComponent} from "../flow-details/flow-details.component";


@Component({
  selector: 'app-package-detail',
  standalone: true,
  templateUrl: './package-detail.component.html',
  imports: [
    MatTableModule,
    MatIconModule,
    MatPaginatorModule
  ],
  styleUrls: ['./package-detail.component.scss']
})
export class PackageDetailComponent implements OnInit {
  packageId: string = '';
  dataSource: MatTableDataSource<any>;
  displayedColumns: string[] = ['position', 'name', 'version', 'modifiedBy', 'modifiedDate', 'actions'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  // Additional fields for package details
  createdBy: string = '';
  creationDate: string = '';
  description: string = '';
  id: string = '';
  mode: string = '';
  modifiedBy: string = '';
  modifiedDate: string = '';
  name: string = '';
  partnerContent: boolean = false;
  resourceId: string = '';
  shortText: string = '';
  supportedPlatform: string = '';
  updateAvailable: boolean = false;
  vendor: string = '';
  version: string = '';


  constructor(
    private route: ActivatedRoute,
    private httpClient: HttpClient,
    private router: Router,
    private dialog: MatDialog // Inject MatDialog
  ) {
    this.dataSource = new MatTableDataSource<any>();
  }

  ngOnInit() {
    this.route.params.subscribe((params) => {
      this.packageId = params['id'];
      this.fetchPackageDetails();
      this.fetchPackageFlows(); // Assuming this method is used to fetch flows
    });
  }

  fetchPackageDetails() {
    const apiUrl = `http://localhost:9001/api/packages/getPackage/${this.packageId}`;

    this.httpClient.get(apiUrl, { responseType: 'json' }).subscribe(
      (response: any) => {
        // Extract details from the response and assign to the corresponding fields
        this.createdBy = response.CreatedBy;
        this.creationDate = response.CreationDate;
        this.description = response.Description;
        this.id = response.Id;
        this.mode = response.Mode;
        this.modifiedBy = response.ModifiedBy;
        this.modifiedDate = response.ModifiedDate;
        this.name = response.Name;
        this.partnerContent = response.PartnerContent;
        this.resourceId = response.ResourceId;
        this.shortText = response.ShortText;
        this.supportedPlatform = response.SupportedPlatform;
        this.updateAvailable = response.UpdateAvailable;
        this.vendor = response.Vendor;
        this.version = response.Version;

        console.log(response);
      },
      (error) => {
        console.error('Error fetching package details:', error);
      }
    );
  }

  fetchPackageFlows() {
    const apiUrl = `http://localhost:9001/api/packages/getPackageFlows/${this.packageId}`;

    this.httpClient.get(apiUrl, { responseType: 'json' }).subscribe(
      (response: any) => {
        // Assuming the response has a 'results' property which is an array
        this.dataSource.data = response.results.map((result: any, index: number) => ({
          position: index + 1,
          name: result.Name,
          version: result.Version,
          modifiedBy: result.ModifiedBy,
          modifiedDate: result.ModifiedDate
        }));

        // Set up paginator after data is loaded
        this.dataSource.paginator = this.paginator;

        console.log(response);
      },
      (error) => {
        console.error('Error fetching package details:', error);
      }
    );
  }

  openFlow(element: any) {
    const flowId = element.name;
    const flowVersion = element.version; // Assuming 'version' is the correct property name

    // Open the flow details dialog
    const dialogRef = this.dialog.open(FlowDetailsComponent, {
      data: {
        name: flowId,
        version: flowVersion, // Pass the version to the dialog
      }
    });

    // Subscribe to dialog close event
    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }






  goBack() {
    // Navigate back to the '/packages' route
    this.router.navigate(['/packages']);
  }
}
