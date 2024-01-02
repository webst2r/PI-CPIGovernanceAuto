import {Component} from '@angular/core';
import {MatTabsModule} from "@angular/material/tabs";
import {GithubCredentialsComponent} from "./github-credentials/github-credentials.component";
import {SapCpiComponent} from "./sap-cpi/sap-cpi.component";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-credentials',
  standalone: true,
  imports: [
    MatTabsModule,
    GithubCredentialsComponent,
    SapCpiComponent,
    TranslateModule
  ],
  templateUrl: './credentials.component.html',
  styleUrls: ['./credentials.component.scss']
})
export class CredentialsComponent {
}
