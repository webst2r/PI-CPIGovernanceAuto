import {Component, inject, OnInit} from '@angular/core';
import {AuthenticationService} from "../../services/authentication.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-logout',
  standalone: true,
  template: '',
  imports: []
})
export class LogoutComponent implements OnInit {
  private auth = inject(AuthenticationService);
  private router = inject(Router);

  ngOnInit(): void {
    this.auth.logout();
    this.router.navigateByUrl('login')
  }

}
