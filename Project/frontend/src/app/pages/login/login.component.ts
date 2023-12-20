import {Component} from '@angular/core';
import {MatInputModule} from "@angular/material/input";
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {Router, RouterLink} from "@angular/router";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AppConstant} from "../../app.constant";
import {AuthenticationService} from "../../services/authentication.service";
import {NgIf} from "@angular/common";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {tap} from "rxjs";
import {ExceptionType} from "../../enumeration/exception";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    MatInputModule,
    MatCardModule,
    MatButtonModule,
    RouterLink,
    ReactiveFormsModule,
    NgIf,
    TranslateModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  error: string = '';
  form: FormGroup = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.pattern(AppConstant.REGEX.email)]),
    password: new FormControl('', [Validators.required, Validators.pattern(AppConstant.REGEX.password)]),
  });

  constructor(private readonly authenticationService: AuthenticationService,
              private router: Router,
              private translate: TranslateService) {
  }

  login() {
    if (!this.form.valid) {
      return;
    }
    this.authenticationService.login({
      email: this.form.controls['email'].value,
      password: this.form.controls['password'].value
    }).pipe(
      tap(user => {
        console.log(user)
        this.authenticationService.saveToken(user.token);
        this.authenticationService.saveUser(user);
      })
    )
      .subscribe({
        next: () => {
          this.router.navigate(['/home'])
        },
        error: (error) => {
          if (error.error && error.error.type === ExceptionType.WRONG_CREDENTIALS) {
            this.error = this.translate.instant("loginPage.wrongCredentials");
          }
        }

      });
  }


}
