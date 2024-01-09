import {Component, Inject, inject, OnInit} from '@angular/core';
import {MatSnackBar} from "@angular/material/snack-bar";
import {PackageDetailService} from "../../services/package-detail.service";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import {NgForOf} from "@angular/common";
import {TranslateModule} from "@ngx-translate/core";
import {RuleFilesService} from "../../services/rule-files.service";
import {RuleFile} from "../../models/rule-file";
import {FlowElement} from "../../models/flows";

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
    TranslateModule
  ],
  templateUrl: './jenkins-dialog.component.html',
  styleUrl: './jenkins-dialog.component.scss'
})
export class JenkinsDialogComponent implements OnInit{
  private snackBar = inject(MatSnackBar);
  ruleFiles: RuleFile[] = [];
  flowElement!: FlowElement | null;

  ngOnInit() {
    this.dialogRef.updateSize('50%', '50%');

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

    if (this.data && this.data.flowElement) {
      this.flowElement = this.data.flowElement;
    }
  }

  form: FormGroup = this.formBuilder.group({
    selectedRuleFile: [null, Validators.required]
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
      const selectedRuleFile = this.form.value.selectedRuleFile;
      console.log('Selected Rule File:', selectedRuleFile);

      this.packageDetailService.enableJenkins(
        this.flowElement.name,
//        selectedRuleFile
      ).subscribe(
        (response) => {
          console.log('Jenkins enabled successfully');
          this.showSuccessToast('Jenkins enabled successfully');
        },
        (error) => {
          console.error('Error enabling Jenkins for the flow:', error);
        }
      );
    }

    this.closeDialog();
  }
}
