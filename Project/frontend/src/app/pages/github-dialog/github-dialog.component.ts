import {Component, inject, Inject, OnInit} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import { TranslateModule } from "@ngx-translate/core";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { MatIconModule } from "@angular/material/icon";
import {NgForOf} from "@angular/common";
import {MatButtonModule} from "@angular/material/button";
import {PackageDetailService} from "../../services/package-detail.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {GithubRepositoryService} from "../../services/github-repository.service";
import {FlowElement} from "../../models/flows";

@Component({
  selector: 'app-github-dialog',
  standalone: true,
  imports: [
    TranslateModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    ReactiveFormsModule,
    NgForOf,
    MatButtonModule,
  ],
  templateUrl: './github-dialog.component.html',
  styleUrls: ['./github-dialog.component.scss']
})
export class GithubDialogComponent implements OnInit {
  private githubRepositoryService = inject(GithubRepositoryService);
  private snackBar = inject(MatSnackBar);
  branches: string[] = [];
  flowElement!: FlowElement | null;

  ngOnInit() {
    this.dialogRef.updateSize('50%', '50%');

    this.fetchBranches();

    if (this.data && this.data.flowElement) {
      this.flowElement = this.data.flowElement;
    }
  }

  form: FormGroup = this.formBuilder.group({
    selectedBranch: [null, Validators.required]
  });

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<GithubDialogComponent>,
    private formBuilder: FormBuilder,
    private packageDetailService: PackageDetailService,
  ) {}

  fetchBranches() {
    this.githubRepositoryService.getBranches().subscribe(
      (branches) => {
        this.branches = branches;
        const initialBranch = this.branches[0];
        this.form.patchValue({ selectedBranch: initialBranch });
      },
      (error) => {
        console.error('Error fetching branches:', error);
      }
    );
  }


  enableGithub() {
    if (this.flowElement) {
      const selectedBranch = this.form.value.selectedBranch;
      console.log('Selected Branch:', selectedBranch);

      this.packageDetailService.enableGithub(
        this.flowElement.name,
        this.flowElement.version,
        selectedBranch  // Pass the selected branch to the backend
      ).subscribe(
        (response) => {
          console.log('Github enabled successfully');
          this.showSuccessToast('Github enabled successfully');
        },
        (error) => {
          console.error('Error enabling Github for the flow:', error);
        }
      );
    }

    this.closeDialog();
  }

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
}
