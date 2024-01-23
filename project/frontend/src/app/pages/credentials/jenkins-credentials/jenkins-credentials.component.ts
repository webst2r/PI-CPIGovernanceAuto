import { Component, computed, effect, inject, Signal, signal, ViewChild } from '@angular/core';
import {FormControl, FormGroup, FormsModule, NgForm, ReactiveFormsModule, Validators} from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatSnackBar } from '@angular/material/snack-bar';
import {catchError, filter, Observable, of, switchMap, tap} from "rxjs";
import { JenkinsCredentials, JenkinsCredentialsCreateRequest, JenkinsCredentialsUpdateRequest } from "../../../models/credentials";
import { toObservable, toSignal } from "@angular/core/rxjs-interop";
import { JenkinsCredentialsService } from "../../../services/jenkins-credentials.service";
import { randomNumber } from "../../../helpers/random-number";
import { ConfirmationDialogService } from "../../../services/confirmation-dialog.service";
import { TranslateModule, TranslateService } from "@ngx-translate/core";

@Component({
  selector: 'app-jenkins-credentials',
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
  templateUrl: './jenkins-credentials.component.html',
  styleUrl: './jenkins-credentials.component.scss'
})
export class JenkinsCredentialsComponent {

  private credentialsService = inject(JenkinsCredentialsService);
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

  credentials$: Observable<JenkinsCredentials | undefined> = toObservable(this.reloadSig)
    .pipe(
      switchMap(_ => this.credentialsService.get())
    );

  credentialsSig: Signal<JenkinsCredentials | undefined> = toSignal(this.credentials$);

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
    const credentialsRequest: JenkinsCredentialsCreateRequest | JenkinsCredentialsUpdateRequest = this.editMode()
      ? { ...formData, id: this.credentialsSig()!.id } as JenkinsCredentialsUpdateRequest
      : formData as JenkinsCredentialsCreateRequest;

    const serviceOperation = this.editMode()
      ? this.credentialsService.update(credentialsRequest as JenkinsCredentialsUpdateRequest)
      : this.credentialsService.create(credentialsRequest as JenkinsCredentialsCreateRequest);

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

    this.confirmDialogService.showDialog(this.translate.instant('credentials.jenkins.confirmation_delete'))
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
