import {Component, HostListener, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {RuleFilesService} from "../../../services/rule-files.service";
import {NgIf} from "@angular/common";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {MatSnackBar} from "@angular/material/snack-bar";
import {TranslateModule, TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-rulefiles',
  standalone: true,
    imports: [
        ReactiveFormsModule,
        NgIf,
        MatInputModule,
        MatButtonModule,
        TranslateModule
    ],
  templateUrl: './rule-files.component.html',
  styleUrl: './rule-files.component.scss'
})
export class RuleFilesComponent implements OnInit{
  private snackBar = inject(MatSnackBar);
  ruleFileForm!: FormGroup;
  translate = inject(TranslateService);

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
      // Check if the file has a valid extension
      if (this.isValidFileExtension(file.name)) {
        this.ruleFileForm.patchValue({
          file
        });
        this.ruleFileForm.get('file')!.updateValueAndValidity();
      } else {
        this.showWarningToast(this.translate.instant('rules.invalid_file_extension_cpi'));
      }
    }
  }

  isValidFileExtension(fileName: string): boolean {
    const allowedExtensions = ['.xml'];
    const fileExtension = fileName.split('.').pop()?.toLowerCase();
    return !!fileExtension && allowedExtensions.includes(`.${fileExtension}`);
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
            this.showWarningToast(this.translate.instant('rules.file_already_exists'));
          } else {
            // File doesn't exist, proceed with the upload
            const formData = new FormData();
            formData.append('file', file);

            this.ruleFilesService.uploadRuleFile(formData).subscribe(
              (response: HttpResponse<any>) => {
                this.showSuccessToast(this.translate.instant('rules.success_upload_cpi'));
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
