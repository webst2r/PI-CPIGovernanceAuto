import { Component } from '@angular/core';
import {MatTabsModule} from "@angular/material/tabs";
import {AllRuleFilesComponent} from "./all-rule-files/all-rule-files.component";
import {RuleFilesComponent} from "./rule-files/rule-files.component";
import {TranslateModule} from "@ngx-translate/core";
import {CodernacFilesComponent} from "./codernac-files/codernac-files.component";
import {MatFormFieldModule} from "@angular/material/form-field";

@Component({
  selector: 'app-rules',
  standalone: true,
    imports: [
        MatTabsModule,
        AllRuleFilesComponent,
        RuleFilesComponent,
        TranslateModule,
        CodernacFilesComponent,
        MatFormFieldModule
    ],
  templateUrl: './rules.component.html',
  styleUrl: './rules.component.scss'
})
export class RulesComponent {

}
