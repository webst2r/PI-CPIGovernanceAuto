import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AppConstant} from "../../app.constant";
import {AuthenticationService} from "../../services/authentication.service";
import {Router} from "@angular/router";
import {TranslateService} from "@ngx-translate/core";

import {tap} from "rxjs";
import {ExceptionType} from "../../enumeration/exception";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {


  error: string = '';
  form: FormGroup = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.pattern(AppConstant.REGEX.email)]),
    password: new FormControl('', [Validators.required, Validators.pattern(AppConstant.REGEX.password)]),
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
  });

  constructor(private readonly authenticationService: AuthenticationService,
              private router: Router,
              private translate: TranslateService) {
  }

  ngOnInit(): void {
  }

  register() {
    if (this.form.invalid) {
      return;
    }

    this.authenticationService.register(this.form.controls['email'].value,
      this.form.controls['password'].value,
      this.form.controls['firstName'].value,
      this.form.controls['lastName'].value,
      ).pipe(
        tap(res => console.log(res))
    ).subscribe(
      () => this.router.navigateByUrl('/login'),
      (error) => {
        if (error.error && error.error.type === ExceptionType.EMAIL_ALREADY_EXISTS) {
          this.error = this.translate.instant("registerPage.emailAlreadyExists");
        }
      }
    );
  }


}
