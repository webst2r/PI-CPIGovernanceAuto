import {Component} from '@angular/core';
import {MatTabsModule} from "@angular/material/tabs";
import {GithubCredentialsComponent} from "./github-credentials/github-credentials.component";
import {SapCpiComponent} from "./sap-cpi/sap-cpi.component";
import {TranslateModule} from "@ngx-translate/core";
import {JenkinsCredentialsComponent} from "./jenkins-credentials/jenkins-credentials.component";
import {MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'app-credentials',
  standalone: true,
  imports: [
    MatTabsModule,
    GithubCredentialsComponent,
    SapCpiComponent,
    TranslateModule,
    JenkinsCredentialsComponent,
    MatIconModule
  ],
  templateUrl: './credentials.component.html',
  styleUrls: ['./credentials.component.scss']
})
export class CredentialsComponent {
}
