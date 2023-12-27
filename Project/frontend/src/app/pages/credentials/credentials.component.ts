import {Component} from '@angular/core';
import {MatTabsModule} from "@angular/material/tabs";
import {GithubCredentialsComponent} from "./github-credentials/github-credentials.component";
import {SapCpiComponent} from "./sap-cpi/sap-cpi.component";

@Component({
  selector: 'app-credentials',
  standalone: true,
  imports: [
    MatTabsModule,
    GithubCredentialsComponent,
    SapCpiComponent
  ],
  templateUrl: './credentials.component.html',
  styleUrls: ['./credentials.component.scss']
})
export class CredentialsComponent {
}
