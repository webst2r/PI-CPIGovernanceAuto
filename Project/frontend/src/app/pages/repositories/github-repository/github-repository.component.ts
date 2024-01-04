import {Component, inject, OnInit, ViewChild, Signal, signal, computed, effect} from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import {catchError, filter, Observable, of, switchMap, tap, throwError} from 'rxjs';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {
  GithubRepository,
  GithubRepositoryCreateRequest,
  GithubRepositoryUpdateRequest
} from '../../../models/repositories';
import { GithubRepositoryService } from '../../../services/github-repository.service';
import {
  GithubCredentials,
} from '../../../models/credentials';
import { GithubCredentialsService } from '../../../services/github-credentials.service';
import { ConfirmationDialogService } from '../../../services/confirmation-dialog.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatChipEditedEvent, MatChipInputEvent, MatChipsModule } from "@angular/material/chips";
import { ENTER, COMMA } from "@angular/cdk/keycodes";
import { MatFormFieldModule } from "@angular/material/form-field";
import { LiveAnnouncer } from "@angular/cdk/a11y";
import { NgForOf } from "@angular/common";
import {randomNumber} from "../../../helpers/random-number";

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
export class GithubRepositoryComponent {
  private credentialsService = inject(GithubCredentialsService);
  private repositoriesService = inject(GithubRepositoryService);
  private confirmDialogService = inject(ConfirmationDialogService);
  private snackBar = inject(MatSnackBar);
  translate = inject(TranslateService);
  @ViewChild('formDirective') private formDirective: any;

  form: FormGroup = new FormGroup({
    name: new FormControl('', [Validators.required]),
    mainBranch: new FormControl('', [Validators.required]),
    secondaryBranches: new FormControl('', [Validators.required]),
    credentials: new FormControl('', [Validators.required]),
  });

  reloadSig = signal<number>(0); // change the value of this signal to reactively update everything on the page

  // Secondary Branches Chips
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  addOnBlur = true;
  announcer = inject(LiveAnnouncer);
  secondaryBranches: Branch[] = [];


  credentialsList: GithubCredentials[] = [];

  repository$: Observable<GithubRepository | undefined> = toObservable(this.reloadSig)
    .pipe(
      switchMap(_ => this.repositoriesService.get())
    );
  repositorySig: Signal<GithubRepository | undefined> = toSignal(this.repository$);
  editMode: Signal<boolean> = computed(() => this.repositorySig() !== undefined && this.repositorySig() !== null);

  constructor() {
    effect(() => {
      // obter as credenciais disponiveis para github
      this.credentialsService.get().subscribe((credentials) => {
        if (credentials) {
          this.credentialsList = [credentials];
        }
      });

      const repository = this.repositorySig();
      if (repository) {
        this.initializeFormValues(this.credentialsList[0], repository);
      }
    });
  }

  private initializeFormValues(credentials: GithubCredentials, repository: GithubRepository): void {
    this.form.get('name')?.setValue(repository.name);
    this.form.get('mainBranch')?.setValue(repository.mainBranch);
    this.form.get('secondaryBranches')?.setValue(repository.secondaryBranches);
    this.form.get('credentials')?.setValue(credentials);
  }

  submit() {
    if (!this.form.valid) {
      console.log("form is not valid");
      return;
    }

    // Extracting form values
    const formData = this.form.value;

    // Create a request object
    const repositoryRequest: GithubRepositoryCreateRequest | GithubRepositoryUpdateRequest = this.editMode()
      ? { ...formData, id: this.repositorySig()!.id } as GithubRepositoryUpdateRequest
      : formData as GithubRepositoryCreateRequest;

    const serviceOperation = this.editMode()
      ? this.repositoriesService.update(repositoryRequest as GithubRepositoryUpdateRequest)
      : this.repositoriesService.create(repositoryRequest as GithubRepositoryCreateRequest);

    serviceOperation.pipe(
      tap(_ => {
        this.reloadSig.set(randomNumber());
        this.showSuccessToast(this.editMode() ? this.translate.instant("repositories.success_update") : this.translate.instant("repositories.success_create"));
      }),
      catchError(error => {
        console.error('Error occurred:', error);
        return throwError(error);
      })
    ).subscribe();

    /*
    // WORKS
    // Create a request object
    const repositoryCreateRequest: GithubRepositoryCreateRequest = {
      name,
      mainBranch,
      secondaryBranches: secondaryBranches.map(branch => branch.name),
      credentials
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
     */
  }

  onDelete() {
    if (!this.repositorySig()) {
      return
    }

    this.confirmDialogService.showDialog(this.translate.instant('repositories.github.confirmation_delete'))
      .pipe(
        filter(res => res.save),
        switchMap(_ => this.delete()),
        catchError(err => {
          console.error('Error opening confirmation dialog:', err);
          return of()
        })
      ).subscribe()
  }

  delete() {
    return this.repositoriesService.delete(this.repositorySig()!.id).pipe(
      tap(_ => {
        this.reloadSig.set(randomNumber());
        this.showSuccessToast(this.translate.instant("repositories.success_delete"));
      }),
    );
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
