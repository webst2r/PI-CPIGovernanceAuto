import {Component, computed, effect, inject, Signal, signal, ViewChild} from '@angular/core';
import {FormControl, FormGroup, NgForm, ReactiveFormsModule, Validators} from "@angular/forms";
import {SapCpiCredentialsService} from "../../../services/sap-cpi-credentials.service";
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {catchError, filter, Observable, of, switchMap, tap} from "rxjs";
import {
  SapCpiCredentials,
  SapCpiCredentialsCreateRequest,
  SapCpiCredentialsUpdateRequest
} from "../../../models/credentials";
import {toObservable, toSignal} from "@angular/core/rxjs-interop";
import {randomNumber} from "../../../helpers/random-number";
import {ConfirmationDialogService} from "../../../services/confirmation-dialog.service";

@Component({
  selector: 'app-sap-cpi',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './sap-cpi.component.html',
  styleUrl: './sap-cpi.component.scss'
})
export class SapCpiComponent {
  private credentialsService = inject(SapCpiCredentialsService);
  private confirmDialogService = inject(ConfirmationDialogService);

  @ViewChild('formDirective') private formDirective!: NgForm;

  hideClientSecret = true;

  form: FormGroup = new FormGroup({
    name: new FormControl('', [Validators.required]),
    baseUrl: new FormControl('', [Validators.required, Validators.pattern(/^(ftp|http|https):\/\/[^ "]+$/)]),
    tokenUrl: new FormControl('', [Validators.required, Validators.pattern(/^(ftp|http|https):\/\/[^ "]+$/)]),
    clientId: new FormControl('', [Validators.required]),
    clientSecret: new FormControl('', [Validators.required]),
  });

  reloadSig = signal<number>(0); //change the value of this signal to reactively update everything on the page

  credentials$: Observable<SapCpiCredentials | undefined> = toObservable(this.reloadSig)
    .pipe(
      switchMap(_ => this.credentialsService.get())
    );

  credentialsSig: Signal<SapCpiCredentials | undefined> = toSignal(this.credentials$);

  editMode: Signal<boolean> = computed(() => this.credentialsSig() !== undefined && this.credentialsSig() !== null);

  constructor() {
    effect(() => {
      const credentials = this.credentialsSig();
      if (credentials) {
        this.form.get('name')?.setValue(credentials.name);
        this.form.get('baseUrl')?.setValue(credentials.baseUrl);
        this.form.get('tokenUrl')?.setValue(credentials.tokenUrl);
        this.form.get('clientId')?.setValue(credentials.clientId);
        this.form.get('clientSecret')?.setValue(credentials.clientSecret);
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
    const credentialsRequest: SapCpiCredentialsCreateRequest | SapCpiCredentialsUpdateRequest = this.editMode()
      ? {...formData, id: this.credentialsSig()!.id} as SapCpiCredentialsUpdateRequest
      : formData as SapCpiCredentialsCreateRequest;

    const serviceOperation = this.editMode()
      ? this.credentialsService.update(credentialsRequest as SapCpiCredentialsUpdateRequest)
      : this.credentialsService.create(credentialsRequest as SapCpiCredentialsCreateRequest);

    serviceOperation.pipe(
      tap(_ => this.reloadSig.set(randomNumber())),
    ).subscribe();
  }

  onDelete() {
    if (!this.credentialsSig()) {
      return
    }

    this.confirmDialogService.showDialog('Are you sure you want to delete the credentials for SAP-CPI?')
      .pipe(
        filter(res => res.save),
        switchMap(_ => this.delete()),
        catchError(err => {
          console.error('Error opening confirmation dialog:', err);
          return of()
        })
      ).subscribe()
  }

  private delete() {
    return this.credentialsService.delete(this.credentialsSig()!.id).pipe(
      tap(_ => this.reloadSig.set(randomNumber())),
    );
  }
}
