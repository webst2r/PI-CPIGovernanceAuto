import { Component } from '@angular/core';
import { MatInputModule } from "@angular/material/input";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { RouterLink } from "@angular/router";
import { TranslateModule } from "@ngx-translate/core";
import {CredentialsService} from "../../services/credentials.service";

@Component({
  selector: 'app-credentials',
  standalone: true,
  imports: [
    MatInputModule,
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    RouterLink,
    TranslateModule
  ],
  templateUrl: './credentials.component.html',
  styleUrls: ['./credentials.component.scss']
})
export class CredentialsComponent {
  error: string = '';
  form: FormGroup = new FormGroup({
    baseUrl: new FormControl('', [Validators.required, Validators.pattern(/^(ftp|http|https):\/\/[^ "]+$/)]),
    tokenUrl: new FormControl('', [Validators.required, Validators.pattern(/^(ftp|http|https):\/\/[^ "]+$/)]),
    clientId: new FormControl('', [Validators.required]),
    clientSecret: new FormControl('', [Validators.required]),
  });

  constructor(private credentialsService: CredentialsService) {}

  registerCredentials() {
    if (this.form.valid) {
      const formData = this.form.value;
      console.log(formData); // You can use formData to send the data to your backend

      // Call the service to send the data to the backend
      this.credentialsService.registerCredentials(formData).subscribe(
        (response) => {
          console.log('Successfully registered credentials:', response);
        },
        (error) => {
          console.error('Error registering credentials:', error);
        }
      );
    }
  }
}
