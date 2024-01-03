import { Component, inject, OnInit, ViewChild } from '@angular/core';
import {FormBuilder, FormGroup, Validators, FormArray, ReactiveFormsModule, FormControl} from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { Observable } from 'rxjs';
import { GithubCredentials } from '../../../models/credentials';
import { GithubCredentialsService } from '../../../services/github-credentials.service';
import { ConfirmationDialogService } from '../../../services/confirmation-dialog.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-github-repository',
  standalone: true,
  imports: [
    MatInputModule,
    MatIconModule,
    TranslateModule,
    MatSelectModule,
    MatButtonModule,
    NgForOf,
    ReactiveFormsModule,
  ],
  templateUrl: './github-repository.component.html',
  styleUrls: ['./github-repository.component.scss'],
})
export class GithubRepositoryComponent implements OnInit {
  private confirmDialogService = inject(ConfirmationDialogService);
  private snackBar = inject(MatSnackBar);
  translate = inject(TranslateService);
  @ViewChild('formDirective') private formDirective: any; // Adjust the type accordingly

  // Use Observable to handle asynchronous data fetching
  credentials$: Observable<GithubCredentials> = this.credentialsService.get();

  credentialsList: GithubCredentials[] = [];

  form: FormGroup = new FormGroup({
    name: new FormControl('', [Validators.required]),
    mainBranch: new FormControl('', [Validators.required]),
    secondaryBranches: new FormControl('', [Validators.required]),
    credentials: new FormControl('', [Validators.required]),
  });


  constructor(private credentialsService: GithubCredentialsService) {
    // Fetch credentials list when the component is initialized
    this.credentialsService.get().subscribe((credentials) => {
      if (credentials) {
        this.credentialsList = [credentials];
      }
    });
  }

  ngOnInit() {
    this.credentialsService.get().subscribe((credentials) => {
      if (credentials) {
        this.credentialsList = [credentials];
      }
    });
  }

  submit() {
    const selectedCredentialControl = this.form.get('credentials');

    if (selectedCredentialControl && selectedCredentialControl.value !== null) {
      const selectedCredentialId = selectedCredentialControl.value;
      // ...

    } else {
      console.error('Form control "credentials" or its value is null');
    }
  }

  credentialsSig() {
    return false;
  }

  onDelete() {
    // Delete logic...
  }

  editMode() {
    return false;
  }
}
