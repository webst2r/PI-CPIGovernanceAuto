import {Component, Input, OnInit} from '@angular/core';
import {MatTableModule} from "@angular/material/table";
import {TranslateModule} from "@ngx-translate/core";
import {CPILintReportDTO} from "../../../models/report";

interface CpiLintReportTableData {
  flowId: string;
  issue: string;
}

@Component({
  selector: 'app-cpi-lint-report',
  standalone: true,
  imports: [
    MatTableModule,
    TranslateModule
  ],
  templateUrl: './cpi-lint-report.component.html',
  styleUrl: './cpi-lint-report.component.scss'
})
export class CpiLintReportComponent implements OnInit {
  @Input() report: CPILintReportDTO | undefined;

  displayedColumns: string[] = ['name', 'issue'];
  dataSource: CpiLintReportTableData[] = [];

  ngOnInit(): void {
    if (this.report?.numberOfIssues !== 0) {
      this.transformIntoTableData(this.report!);
    }
  }

  private transformIntoTableData(report: CPILintReportDTO) {
    report.issues.forEach(issue => {
      this.dataSource.push({
        flowId: issue.flowId,
        issue: issue.issue,
      });
    });
  }
}
