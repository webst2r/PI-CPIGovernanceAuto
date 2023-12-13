import {Component} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatCardModule} from "@angular/material/card";
import {MatInputModule} from "@angular/material/input";
import {NgIf} from "@angular/common";
import {Router, RouterLink} from "@angular/router";
import {MatButtonModule} from "@angular/material/button";
import {AuthenticationService} from "../../services/authentication.service";
import {AppConstant} from "../../app.constant";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {ExceptionType} from "../../enumeration/exception";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    NgIf,
    RouterLink,
    MatButtonModule,
    TranslateModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  error: string = '';
  form: FormGroup = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.pattern(AppConstant.REGEX.email)]),
    password: new FormControl('', [Validators.required, Validators.pattern(AppConstant.REGEX.password)]),
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
  });

  constructor(private readonly authenticationService: AuthenticationService,
              private router: Router,
              private translate:TranslateService) {
  }

  register() {
    if (!this.form.valid) {
      return;
    }
    this.authenticationService.register(
      {
        email: this.form.controls['email'].value,
        password: this.form.controls['password'].value,
        firstname: this.form.controls['firstName'].value,
        lastname: this.form.controls['lastName'].value,
      }
    )
      .subscribe({
        next: () => this.router.navigate(['login']),
        error: (error) => {
          if (error.error && error.error.type === ExceptionType.EMAIL_ALREADY_EXISTS) {
            this.error = this.translate.instant("registerPage.emailAlreadyExists");
          }
        }

      });

  }
}
