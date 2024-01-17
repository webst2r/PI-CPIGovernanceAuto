import {Component, computed, inject} from '@angular/core';
import {TranslateModule} from "@ngx-translate/core";
import {navbarData} from "../../sidenav/nav-data";
import {RouterLink} from "@angular/router";
import {AuthenticationService} from "../../services/authentication.service";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    TranslateModule,
    RouterLink,
    NgForOf
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  private auth = inject(AuthenticationService)

  navData = computed(() => {
    return navbarData.filter((data) =>
      data.authRequired === this.auth.isLoggedInSig())
  });

}
