import {inject, Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmationDialogComponent} from "../components/confirmation-dialog/confirmation-dialog.component";

@Injectable({
  providedIn: 'root'
})
export class ConfirmationDialogService {

  private dialog = inject(MatDialog);

  showDialog(text: string = ''): Observable<{ save: boolean }> {
    const dialog = this.dialog.open(ConfirmationDialogComponent, {
      disableClose: true,
      autoFocus: false,
      restoreFocus: false,
      width: '40vw',
      data: {text},
    });

    return dialog.afterClosed();
  }
}
