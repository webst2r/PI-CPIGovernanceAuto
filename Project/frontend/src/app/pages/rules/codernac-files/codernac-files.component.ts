import {Component, HostListener, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgIf} from "@angular/common";
import {MatSnackBar} from "@angular/material/snack-bar";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {CodenarcFilesService} from "../../../services/codenarc-files.service";
import {MatFormFieldModule} from "@angular/material/form-field";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-codernac-files',
  standalone: true,
    imports: [
        FormsModule,
        NgIf,
        ReactiveFormsModule,
        MatFormFieldModule,
        TranslateModule
    ],
  templateUrl: './codernac-files.component.html',
  styleUrl: './codernac-files.component.scss'
})
export class CodernacFilesComponent implements OnInit{
  private snackBar = inject(MatSnackBar);
  codenarcFileForm!: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private codenarcFilesService: CodenarcFilesService
  ) { }

  ngOnInit() {
    this.codenarcFileForm = this.formBuilder.group({
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
      this.codenarcFileForm.patchValue({
        file
      });
      this.codenarcFileForm.get('file')!.updateValueAndValidity();
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
    const file = this.codenarcFileForm.get('file')!.value;

    if (file) {
      // Check if the file already exists
      this.codenarcFilesService.checkFileExists(file.name).subscribe(
        (exists: boolean) => {
          if (exists) {
            this.showWarningToast(`File "${file.name}" already exists. Please choose a different file.`);
          } else {
            const formData = new FormData();
            formData.append('file', file);

            this.codenarcFilesService.uploadCodenarcFile(formData).subscribe(
              (response: HttpResponse<any>) => {
                this.showSuccessToast(`Codenarc file "${file.name}" uploaded successfully`);
              },
              (error: HttpErrorResponse) => {
                console.error('Error uploading Codenarc file:', error);
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
