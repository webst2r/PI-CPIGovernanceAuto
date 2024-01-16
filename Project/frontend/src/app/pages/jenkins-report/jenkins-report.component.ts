import {Component, inject} from '@angular/core';
import {TranslateModule} from "@ngx-translate/core";
import {MatTabsModule} from "@angular/material/tabs";
import {CpiLintReportComponent} from "./cpi-lint-report/cpi-lint-report.component";
import {CodenarcReportComponent} from "./codenarc-report/codenarc-report.component";
import {DependencyCheckReportComponent} from "./dependency-check-report/dependency-check-report.component";
import {toSignal} from "@angular/core/rxjs-interop";
import {ReportService} from "../../services/report.service";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-jenkins-report',
  standalone: true,
  imports: [
    TranslateModule,
    MatTabsModule,
    CpiLintReportComponent,
    CodenarcReportComponent,
    DependencyCheckReportComponent,
    NgIf
  ],
  templateUrl: './jenkins-report.component.html',
  styleUrl: './jenkins-report.component.scss'
})
export class JenkinsReportComponent {
  reportService = inject(ReportService);
  reportSig = toSignal(this.reportService.get());
}
