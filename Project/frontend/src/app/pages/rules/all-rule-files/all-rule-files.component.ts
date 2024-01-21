import {Component, inject, OnInit} from '@angular/core';
import {RuleFile} from "../../../models/rule-file";
import {RuleFilesService} from "../../../services/rule-files.service";
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {MatTooltipModule} from "@angular/material/tooltip";
import {catchError, filter, of, switchMap} from "rxjs";
import {ConfirmationDialogService} from "../../../services/confirmation-dialog.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-all-rule-files',
  standalone: true,
  templateUrl: './all-rule-files.component.html',
  imports: [
    MatTableModule,
    MatIconModule,
    MatButtonModule,
    TranslateModule,
    MatTooltipModule
  ],
  styleUrls: ['./all-rule-files.component.scss']
})
export class AllRuleFilesComponent implements OnInit {
  ruleFilesDataSource = new MatTableDataSource<RuleFile>();
  codenarcFilesDataSource = new MatTableDataSource<RuleFile>();
  private confirmDialogService = inject(ConfirmationDialogService);
  translate = inject(TranslateService);

  constructor(private ruleFilesService: RuleFilesService,
              private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.loadAllFiles();
  }

  loadAllFiles(): void {
    this.ruleFilesService.getAllRuleAndCodenarcFiles().subscribe({
      next: (data) => {
        this.ruleFilesDataSource.data = data.ruleFiles;
        this.codenarcFilesDataSource.data = data.codenarcFiles;
        console.log("Successfully loaded files:", data);
      },
      error: (error) => {
        console.error('Error loading files:', error);
      }
    });
  }

  deleteCodenarcFile(id: string) {
    this.confirmDialogService
      .showDialog(this.translate.instant('rules.codenarc_confirmation_delete'))
      .pipe(
        filter((res) => res.save),
        switchMap((_) => this.ruleFilesService.deleteCodenarcFile(id)),
        catchError((error) => {
          console.error('Error deleting Codenarc file:', error);
          return of();
        })
      )
      .subscribe(() => {
        // Reload the Codenarc files after successful deletion
        this.loadAllFiles();
        this.showSuccessToast(this.translate.instant('rules.success_delete_codenarc'));
      });
  }

  deleteRuleFile(id: string) {
    this.confirmDialogService
      .showDialog(this.translate.instant('rules.cpi_confirmation_delete'))
      .pipe(
        filter((res) => res.save),
        switchMap((_) => this.ruleFilesService.deleteRuleFile(id)),
        catchError((error) => {
          console.error('Error deleting Rule file:', error);
          return of();
        })
      )
      .subscribe(() => {
        // Reload the Rule files after successful deletion
        this.loadAllFiles();
        this.showSuccessToast(this.translate.instant('rules.success_delete_cpi'));
      });
  }

  showSuccessToast(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000,
      panelClass: 'success-toast',
    });
  }
}
