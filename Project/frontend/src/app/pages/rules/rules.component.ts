import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {RuleFilesService} from "../../services/rule-files.service";
import {NgIf} from "@angular/common";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-rulefiles',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './rules.component.html',
  styleUrl: './rules.component.scss'
})
export class RulesComponent {
  ruleFileForm!: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private ruleFilesService: RuleFilesService // Inject your service
  ) { }

  ngOnInit() {
    this.ruleFileForm = this.formBuilder.group({
      file: [null, Validators.required]
    });
  }

  onFileChange(event: any) {
    const file = (event.target as HTMLInputElement).files![0];
    this.ruleFileForm.patchValue({
      file
    });
    this.ruleFileForm.get('file')!.updateValueAndValidity();
  }

  onSubmit() {
    const file = this.ruleFileForm.get('file')!.value;

    if (file) {
      const formData = new FormData();
      formData.append('file', file);

      this.ruleFilesService.uploadRuleFile(formData).subscribe(
        (response: HttpResponse<any>) => {
          console.log('Rule file uploaded successfully:', response);
          // You can add success notifications or additional logic here
        },
        (error: HttpErrorResponse) => {
          console.error('Error uploading rule file:', error);
          // Handle the error and display notifications if needed
        }
      );
    }
  }
}
