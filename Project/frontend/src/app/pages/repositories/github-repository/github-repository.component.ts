import {Component, inject, OnInit, ViewChild, Signal, signal, computed} from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { Observable } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { GithubRepository, GithubRepositoryCreateRequest } from '../../../models/repositories';
import { GithubRepositoryService } from '../../../services/github-repository.service';
import { GithubCredentials } from '../../../models/credentials';
import { GithubCredentialsService } from '../../../services/github-credentials.service';
import { ConfirmationDialogService } from '../../../services/confirmation-dialog.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatChipEditedEvent, MatChipInputEvent, MatChipsModule } from "@angular/material/chips";
import { ENTER, COMMA } from "@angular/cdk/keycodes";
import { MatFormFieldModule } from "@angular/material/form-field";
import { LiveAnnouncer } from "@angular/cdk/a11y";
import { NgForOf } from "@angular/common";

interface Branch {
  name: string;
}

@Component({
  selector: 'app-github-repository',
  standalone: true,
  imports: [
    MatInputModule,
    MatIconModule,
    TranslateModule,
    MatSelectModule,
    MatButtonModule,
    MatChipsModule,
    MatFormFieldModule,
    NgForOf,
    ReactiveFormsModule,
  ],
  templateUrl: './github-repository.component.html',
  styleUrls: ['./github-repository.component.scss'],
})
export class GithubRepositoryComponent implements OnInit {
  private credentialsService = inject(GithubCredentialsService);
  private repositoriesService = inject(GithubRepositoryService);
  private confirmDialogService = inject(ConfirmationDialogService);
  private snackBar = inject(MatSnackBar);
  translate = inject(TranslateService);

  // Secondary Branches Chips
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  addOnBlur = true;
  announcer = inject(LiveAnnouncer);
  secondaryBranches: Branch[] = [];

  @ViewChild('formDirective') private formDirective: any;

  // Use Observable to handle asynchronous data fetching
  credentials$: Observable<GithubCredentials> = this.credentialsService.get();

  credentialsList: GithubCredentials[] = [];

  // Repository Signal
  repositorySig: Signal<GithubRepository | undefined> = signal<GithubRepository | undefined>(undefined);

  editMode: Signal<boolean> = computed(() => this.repositorySig() !== undefined && this.repositorySig() !== null);

  form: FormGroup = new FormGroup({
    name: new FormControl('', [Validators.required]),
    mainBranch: new FormControl('', [Validators.required]),
    secondaryBranches: new FormControl('', [Validators.required]),
    credentials: new FormControl('', [Validators.required]),
  });

  ngOnInit() {
    this.credentialsService.get().subscribe((credentials) => {
      if (credentials) {
        this.credentialsList = [credentials];
      }
    });

    this.repositoriesService.get().subscribe((repository) => {
      if (repository) {
        this.repositorySig();  // Set the value of the repository signal
        this.initializeFormValues(this.credentialsList[0], repository);
      }
    });
  }

  private initializeFormValues(credentials: GithubCredentials, repository: GithubRepository): void {
    this.form.get('name')?.setValue(repository.name);
    this.form.get('mainBranch')?.setValue(repository.mainBranch);
    this.form.get('credentials')?.setValue(credentials);
  }

  submit() {
    if (!this.form.valid) {
      return;
    }

    // Extracting form values
    const name = this.form.get('name')?.value;
    const mainBranch = this.form.get('mainBranch')?.value;
    const credentials = this.form.get('credentials')?.value;
    // Handle other form controls as needed

    // Create a request object
    const repositoryCreateRequest: GithubRepositoryCreateRequest = {
      name,
      mainBranch,
      credentials,
      secondaryBranches: this.secondaryBranches.map(branch => branch.name),
    };

    // Call the service function to create the repository
    this.repositoriesService.create(repositoryCreateRequest).subscribe(
      (createdRepository) => {
        // Handle the success case
        console.log('Repository created successfully:', createdRepository);
        this.showSuccessToast('Repository created successfully');
        this.repositorySig();
        // Optionally, you can reset the form after a successful submission
        this.formDirective.resetForm();
      },
      (error) => {
        // Handle the error case
        console.error('Error creating repository:', error);
        // Optionally, display an error message to the user
      }
    );
  }

  onDelete() {
    const currentRepository = this.repositorySig();  // Get the current value of the repository signal
    if (currentRepository) {
      // Delete logic
    }
  }

  showSuccessToast(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000,
      panelClass: 'success-toast',
    }).onAction().subscribe(() => this.snackBar.dismiss());
  }

  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    // Add our branch
    if (value) {
      this.secondaryBranches.push({ name: value });
    }

    // Clear the input value
    event.chipInput!.clear();
  }

  remove(branch: Branch): void {
    const index = this.secondaryBranches.indexOf(branch);

    if (index >= 0) {
      this.secondaryBranches.splice(index, 1);

      this.announcer.announce(`Removed ${branch}`);
    }
  }

  edit(branch: Branch, event: MatChipEditedEvent) {
    const value = event.value.trim();

    // Remove branch if it no longer has a name
    if (!value) {
      this.remove(branch);
      return;
    }

    // Edit existing branch
    const index = this.secondaryBranches.indexOf(branch);
    if (index >= 0) {
      this.secondaryBranches[index].name = value;
    }
  }
}
