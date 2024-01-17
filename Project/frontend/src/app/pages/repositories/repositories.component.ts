import { Component } from '@angular/core';
import {TranslateModule} from "@ngx-translate/core";
import {GithubCredentialsComponent} from "../credentials/github-credentials/github-credentials.component";
import {JenkinsCredentialsComponent} from "../credentials/jenkins-credentials/jenkins-credentials.component";
import {MatIconModule} from "@angular/material/icon";
import {MatTabsModule} from "@angular/material/tabs";
import {SapCpiComponent} from "../credentials/sap-cpi/sap-cpi.component";
import {GithubRepositoryComponent} from "./github-repository/github-repository.component";

@Component({
  selector: 'app-repositories',
  standalone: true,
  imports: [
    TranslateModule,
    GithubCredentialsComponent,
    JenkinsCredentialsComponent,
    MatIconModule,
    MatTabsModule,
    SapCpiComponent,
    GithubRepositoryComponent
  ],
  templateUrl: './repositories.component.html',
  styleUrl: './repositories.component.scss'
})
export class RepositoriesComponent {

}
