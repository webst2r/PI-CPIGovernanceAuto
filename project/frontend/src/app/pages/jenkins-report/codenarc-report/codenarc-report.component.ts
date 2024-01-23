import {Component, Input, OnInit} from '@angular/core';
import {MatTableModule} from "@angular/material/table";
import {TranslateModule} from "@ngx-translate/core";
import {CodenarcReportDTO} from "../../../models/report";

interface CodenarcReportTableData {
  name: string;
  ruleName: string;
  priority: number;
  lineNumber: number;
}

@Component({
  selector: 'app-codenarc-report',
  standalone: true,
  imports: [
    MatTableModule,
    TranslateModule
  ],
  templateUrl: './codenarc-report.component.html',
  styleUrl: './codenarc-report.component.scss'
})
export class CodenarcReportComponent implements OnInit {
  @Input() report: CodenarcReportDTO | undefined;

  nbViolations = 0;
  nbFilesWithViolations = 0;

  displayedColumns: string[] = ['fileName', 'line', 'priority', 'violation'];
  dataSource: CodenarcReportTableData[] = [];

  ngOnInit(): void {
    if (this.report?.summary.totalFiles !== 0 && this.report?.summary.filesWithViolations !== 0) {
      this.transformIntoTableData(this.report!);
      this.nbFilesWithViolations = this.report!.summary.filesWithViolations;
      this.nbViolations = this.report!.summary.priority1 + this.report!.summary.priority2 + this.report!.summary.priority3;
    }
  }

  private transformIntoTableData(report: CodenarcReportDTO) {
    report.packages.forEach(packageItem => {
      packageItem.files.forEach(file => {
        file.violations.forEach(violation => {
          this.dataSource.push({
            name: file.name,
            ruleName: violation.ruleName,
            priority: violation.priority,
            lineNumber: violation.lineNumber,
          });
        });
      });
    });
  }
}
