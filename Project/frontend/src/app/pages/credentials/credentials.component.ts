import { Component } from '@angular/core';
import { MatInputModule } from "@angular/material/input";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatIconModule } from "@angular/material/icon";
import { AppConstant } from "../../app.constant";
import {MatButtonModule} from "@angular/material/button";
import {RouterLink} from "@angular/router";
import {TranslateModule} from "@ngx-translate/core";

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
  form: FormGroup = new FormGroup({
    baseUrl: new FormControl('', [Validators.required]),
    tokenUrl: new FormControl('', [Validators.required]),
    tokenId: new FormControl('', [Validators.required]),
    clientId: new FormControl('', [Validators.required]),
  });

  registerCredentials() {
    // Handle form submission logic here
    if (this.form.valid) {
      const formData = this.form.value;
      console.log(formData); // You can use formData to send the data to your backend
    }
  }
}
