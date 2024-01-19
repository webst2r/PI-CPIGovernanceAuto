import {Component, Inject, inject, OnInit, signal} from '@angular/core';
import {MatSnackBar} from "@angular/material/snack-bar";
import {PackageDetailService} from "../../services/package-detail.service";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import {NgForOf, NgIf} from "@angular/common";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {RuleFilesService} from "../../services/rule-files.service";
import {CodenarcFile, RuleFile} from "../../models/rule-file";
import {FlowElement} from "../../models/flows";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {ReportService} from "../../services/report.service";
import {Router} from "@angular/router";
import {Observable, tap} from "rxjs";

@Component({
  selector: 'app-jenkins-dialog',
  standalone: true,
  imports: [
    FormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatOptionModule,
    MatSelectModule,
    NgForOf,
    ReactiveFormsModule,
    TranslateModule,
    MatProgressSpinnerModule,
    NgIf
  ],
  templateUrl: './jenkins-dialog.component.html',
  styleUrl: './jenkins-dialog.component.scss'
})
export class JenkinsDialogComponent implements OnInit{
  private snackBar = inject(MatSnackBar);
  private reportService= inject(ReportService);
  ruleFiles: RuleFile[] = [];
  codenarcFiles: CodenarcFile[] = [];
  flowElement!: FlowElement | null;
  isLoadingSig = signal(false);
  router = inject(Router);
  private translate = inject(TranslateService);
  downloadedZipFile!: Blob;

  ngOnInit() {
    this.dialogRef.updateSize('65%', '65%');

    // Fetch rule files from the backend
    this.ruleFilesService.getAllRuleFiles().subscribe(
      (ruleFiles) => {
        this.ruleFiles = ruleFiles;
        console.log('Rule files:', this.ruleFiles);
        if (this.ruleFiles.length > 0) {
          const initialRuleFile = this.ruleFiles[0].fileName;
          this.form.patchValue({ selectedRuleFile: initialRuleFile });
        }
      },
      (error) => {
        console.error('Error fetching rule files:', error);
      }
    );

    // Fetch codenarc files from the backend
    this.ruleFilesService.getAllCodenarcFiles().subscribe(
      (codenarcFiles) => {
        this.codenarcFiles = codenarcFiles;
        console.log('Codenarc files:', this.codenarcFiles);
        if (this.codenarcFiles.length > 0) {
          const initialCodenarcFile = this.codenarcFiles[0].fileName;
          this.form.patchValue({ selectedCodenarcFile: initialCodenarcFile });
        }
      },
      (error) => {
        console.error('Error fetching codenarc files:', error);
      }
    );

    if (this.data && this.data.flowElement) {
      this.flowElement = this.data.flowElement;
    }
  }

  form: FormGroup = this.formBuilder.group({
    selectedRuleFile: [null, Validators.required],
    selectedCodenarcFile: [null, Validators.required]
  });

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<JenkinsDialogComponent>,
    private formBuilder: FormBuilder,
    private packageDetailService: PackageDetailService,
    private ruleFilesService: RuleFilesService,
  ) {}

  showSuccessToast(message: string): void {
    this.snackBar
      .open(message, 'Close', {
        duration: 5000,
        panelClass: 'success-toast',
      })
      .onAction()
      .subscribe(() => this.snackBar.dismiss());
  }

  closeDialog() {
    this.dialogRef.close();
  }

  enableJenkins() {
    if (this.flowElement) {
      this.isLoadingSig.set(true);
      const selectedRuleFile = this.form.value.selectedRuleFile;
      const selectedCodenarcFile = this.form.value.selectedCodenarcFile;
      console.log('Selected Rule File:', selectedRuleFile);
      console.log('Selected Codenarc File:', selectedCodenarcFile);

      this.uploadFlowZip(this.flowElement);

      let flowName = this.removeSpaces(this.flowElement.name)
      this.packageDetailService.enableJenkins(flowName, selectedRuleFile, selectedCodenarcFile, this.flowElement.version).pipe(
        tap(res => this.reportService.set(res))
      )
        .subscribe(
        (response) => {
          this.isLoadingSig.set(false);
          this.router.navigateByUrl('packages/jenkins/report');
          this.closeDialog();
          //this.showSuccessToast('Jenkins enabled successfully');
        },
        (error) => {
          console.error('Error enabling Jenkins for the flow:', error);
          this.showSuccessToast(this.translate.instant('package.jenkins_error'));
          this.closeDialog();
        }
      );
    }

  }
  removeSpaces(text: string): string {
    // Use a regular expression to match and replace spaces globally
    return text.replace(/\s/g, '');
  }

  downloadFlowNoTransfer(element: FlowElement): Observable<any> {
    return new Observable((observer) => {
      this.packageDetailService.downloadFlow(element.id, element.version).subscribe(
        (response) => {
          const contentType = response.type;
          if (contentType === 'application/zip') {
            this.downloadedZipFile = response;
            observer.next(); // Notify the observer that the download is complete
            observer.complete();
          } else {
            observer.error(`Unexpected content type: ${contentType}`);
          }
        },
        (error) => {
          observer.error(`Error downloading flow: ${error}`);
        }
      );
    });
  }

  uploadFlowZip(element: FlowElement) {
    this.downloadFlowNoTransfer(element).subscribe(
      () => {
        // Now the download is complete, proceed with the upload
        if (this.downloadedZipFile) {
          let flowName = this.removeSpaces(element.name);
          this.packageDetailService.uploadFlowZip(flowName, this.downloadedZipFile).subscribe(
            () => {
              this.showSuccessToast(`${element.name}_${element.version}.zip uploaded successfully`);
            },
            (error) => {
              console.error('Error uploading flow:', error);
            }
          );
        } else {
          console.error('No file downloaded yet.');
        }
      },
      (error) => {
        console.error(error); // Handle any errors during download
      }
    );
  }

}
