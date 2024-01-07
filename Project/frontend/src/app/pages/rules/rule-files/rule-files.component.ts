import {Component, HostListener, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {RuleFilesService} from "../../../services/rule-files.service";
import {NgIf} from "@angular/common";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-rulefiles',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './rule-files.component.html',
  styleUrl: './rule-files.component.scss'
})
export class RuleFilesComponent implements OnInit{
  private snackBar = inject(MatSnackBar);
  ruleFileForm!: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private ruleFilesService: RuleFilesService
  ) { }

  ngOnInit() {
    this.ruleFileForm = this.formBuilder.group({
      file: [null, Validators.required]
    });
  }

  onFileChange(event: any) {
    this.handleFile(event.target.files[0]);
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    const file = event.dataTransfer?.files[0];
    this.handleFile(file);
  }

  handleFile(file: File | undefined) {
    if (file) {
      this.ruleFileForm.patchValue({
        file
      });
      this.ruleFileForm.get('file')!.updateValueAndValidity();
    }
  }

  @HostListener('dragover', ['$event'])
  onDragOver(event: DragEvent) {
    event.preventDefault();
  }

  @HostListener('drop', ['$event'])
  onDropFile(event: DragEvent) {
    this.onDrop(event);
  }

  onSubmit() {
    const file = this.ruleFileForm.get('file')!.value;

    if (file) {
      // Check if the file already exists
      this.ruleFilesService.checkFileExists(file.name).subscribe(
        (exists: boolean) => {
          if (exists) {
            // File already exists, show a warning message
            this.showWarningToast(`File "${file.name}" already exists. Please choose a different file.`);
          } else {
            // File doesn't exist, proceed with the upload
            const formData = new FormData();
            formData.append('file', file);

            this.ruleFilesService.uploadRuleFile(formData).subscribe(
              (response: HttpResponse<any>) => {
                this.showSuccessToast(`Rule file "${file.name}" uploaded successfully`);
              },
              (error: HttpErrorResponse) => {
                console.error('Error uploading rule file:', error);
              }
            );
          }
        },
        (error: HttpErrorResponse) => {
          console.error('Error checking file existence:', error);
        }
      );
    }
  }

  showWarningToast(message: string): void {
    this.snackBar
      .open(message, 'Close', {
        duration: 5000,
        panelClass: 'warning-toast',
      })
      .onAction()
      .subscribe(() => this.snackBar.dismiss());
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
}
