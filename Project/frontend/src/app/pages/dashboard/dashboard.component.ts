// dashboard.component.ts
import { Component } from '@angular/core';
import {AuthenticationService} from "../../services/authentication.service";
import {Router, RouterLink} from "@angular/router";
import {NgIf} from "@angular/common";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {MatInputModule} from "@angular/material/input";
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'app-dashboard',
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
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent {
  constructor(private readonly authenticationService: AuthenticationService,
              private router: Router,
              private translate: TranslateService) {
  }



  isAuthenticated(): boolean {
    return this.authenticationService.isAuthenticated(); // Implement your own logic here
  }
}
