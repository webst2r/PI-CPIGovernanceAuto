import {Component, Input, OnInit} from '@angular/core';
import {MatTableModule} from "@angular/material/table";
import {TranslateModule} from "@ngx-translate/core";
import {DependencyCheckReportDTO} from '../../../models/report';
import {CommonModule} from "@angular/common";

interface DependencyCheckReportTableData {
  fileName: string;
  name: string;
  severity: string;
}

@Component({
  selector: 'app-dependency-check-report',
  standalone: true,
  imports: [
    MatTableModule,
    TranslateModule,
    CommonModule
  ],
  templateUrl: './dependency-check-report.component.html',
  styleUrl: './dependency-check-report.component.scss'
})
export class DependencyCheckReportComponent implements OnInit {
  @Input() report: DependencyCheckReportDTO | undefined;

  displayedColumns: string[] = ['jarName', 'vulnerability', 'severity'];
  dataSource: DependencyCheckReportTableData[] = [];

  ngOnInit(): void {
    if (this.report?.dependencies.length !== 0) {
      this.transformIntoTableData(this.report!);
    }
  }

  private transformIntoTableData(report: DependencyCheckReportDTO) {
    report.dependencies.forEach(dependency => {
      dependency.vulnerabilities.forEach(vulnerability => {
        this.dataSource.push({
          fileName: dependency.fileName,
          name: vulnerability.name,
          severity: vulnerability.severity,
        });
      });
    });
  }
  getSeverityColor(severity: string): string {
    switch (severity) {
      case 'LOW':
        return 'green';
      case 'MEDIUM':
        return 'orange';
      case 'HIGH':
        return 'red';
      default:
        return 'black'; // or any default color
    }
  }
}
