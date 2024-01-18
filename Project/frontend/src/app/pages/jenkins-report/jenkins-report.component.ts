import {Component, inject} from '@angular/core';
import {TranslateModule} from "@ngx-translate/core";
import {MatTabsModule} from "@angular/material/tabs";
import {CpiLintReportComponent} from "./cpi-lint-report/cpi-lint-report.component";
import {CodenarcReportComponent} from "./codenarc-report/codenarc-report.component";
import {DependencyCheckReportComponent} from "./dependency-check-report/dependency-check-report.component";
import {ReportService} from "../../services/report.service";
import {AsyncPipe, NgIf} from "@angular/common";
import {ReportDTO} from "../../models/report";

@Component({
  selector: 'app-jenkins-report',
  standalone: true,
  imports: [
    TranslateModule,
    MatTabsModule,
    CpiLintReportComponent,
    CodenarcReportComponent,
    DependencyCheckReportComponent,
    NgIf,
    AsyncPipe
  ],
  templateUrl: './jenkins-report.component.html',
  styleUrl: './jenkins-report.component.scss'
})
export class JenkinsReportComponent {
  reportService = inject(ReportService);
  report: ReportDTO | undefined;

  ngOnInit(): void {
    this.reportService.get().subscribe((data) => {
      this.report = data;
    });
  }



}
