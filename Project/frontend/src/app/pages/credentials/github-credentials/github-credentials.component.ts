import { Component, computed, effect, inject, Signal, signal, ViewChild } from '@angular/core';
import {FormControl, FormGroup, FormsModule, NgForm, ReactiveFormsModule, Validators} from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatSnackBar } from '@angular/material/snack-bar';
import {catchError, filter, Observable, of, switchMap, tap} from "rxjs";
import { GithubCredentials, GithubCredentialsCreateRequest, GithubCredentialsUpdateRequest } from "../../../models/credentials";
import { toObservable, toSignal } from "@angular/core/rxjs-interop";
import { GithubCredentialsService } from "../../../services/github-credentials.service";
import { randomNumber } from "../../../helpers/random-number";
import { ConfirmationDialogService } from "../../../services/confirmation-dialog.service";
import { TranslateModule, TranslateService } from "@ngx-translate/core";

@Component({
  selector: 'app-github-credentials',
  standalone: true,
  imports: [
    FormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    ReactiveFormsModule,
    TranslateModule
  ],
  templateUrl: './github-credentials.component.html',
  styleUrl: './github-credentials.component.scss'
})
export class GithubCredentialsComponent {
  private credentialsService = inject(GithubCredentialsService);
  private confirmDialogService = inject(ConfirmationDialogService);
  private snackBar = inject(MatSnackBar);
  translate = inject(TranslateService);
  // TODO: add toast with messages
  @ViewChild('formDirective') private formDirective!: NgForm;

  hideAccessToken = true;

  form: FormGroup = new FormGroup({
    name: new FormControl('', [Validators.required]),
    username: new FormControl('', [Validators.required]),
    accessToken: new FormControl('', [Validators.required]),
  });

  reloadSig = signal<number>(0); // change the value of this signal to reactively update everything on the page

  credentials$: Observable<GithubCredentials | undefined> = toObservable(this.reloadSig)
    .pipe(
      switchMap(_ => this.credentialsService.get())
    );

  credentialsSig: Signal<GithubCredentials | undefined> = toSignal(this.credentials$);

  editMode: Signal<boolean> = computed(() => this.credentialsSig() !== undefined && this.credentialsSig() !== null);

  constructor() {
    effect(() => {
      const credentials = this.credentialsSig();
      if (credentials) {
        this.form.get('name')?.setValue(credentials.name);
        this.form.get('username')?.setValue(credentials.username);
        this.form.get('accessToken')?.setValue(credentials.accessToken);
      } else {
        this.formDirective.resetForm();
      }
    });
  }

  submit() {
    if (!this.form.valid) {
      return;
    }

    const formData = this.form.value;
    const credentialsRequest: GithubCredentialsCreateRequest | GithubCredentialsUpdateRequest = this.editMode()
      ? { ...formData, id: this.credentialsSig()!.id } as GithubCredentialsUpdateRequest
      : formData as GithubCredentialsCreateRequest;

    const serviceOperation = this.editMode()
      ? this.credentialsService.update(credentialsRequest as GithubCredentialsUpdateRequest)
      : this.credentialsService.create(credentialsRequest as GithubCredentialsCreateRequest);

    serviceOperation.pipe(
      tap(_ => {
        this.reloadSig.set(randomNumber());
        this.showSuccessToast(this.editMode() ? this.translate.instant("credentials.success_update") : this.translate.instant("credentials.success_create"));
      }),
    ).subscribe();
  }

  onDelete() {
    if (!this.credentialsSig()) {
      return
    }

    this.confirmDialogService.showDialog(this.translate.instant('credentials.github.confirmation_delete'))
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
    return this.credentialsService.delete(this.credentialsSig()!.id).pipe(
      tap(_ => {
        this.reloadSig.set(randomNumber());
        this.showSuccessToast(this.translate.instant("credentials.success_delete"));
      }),
    );
  }

  showSuccessToast(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000,
      panelClass: 'success-toast',
    }).onAction().subscribe(() => this.snackBar.dismiss());
  }
}
