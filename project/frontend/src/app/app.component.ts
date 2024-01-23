import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterOutlet} from '@angular/router';
import {SidenavComponent} from "./sidenav/sidenav.component";
import {BodyComponent} from "./pages/body/body.component";
import {StorageKey, StorageService} from "./services/storage.service";
import {TranslateService} from "@ngx-translate/core";

interface SideNavToggle {
  screenWidth: number;
  collapsed: boolean;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, SidenavComponent, BodyComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  private storageService = inject(StorageService)
  private translateService = inject(TranslateService)

  constructor() {
    const lang = this.storageService.getData(StorageKey.LANGUAGE);
    if (lang) {
      this.translateService.use(lang);
    }
  }

  isSideNavCollapsed = false;
  screenWidth = 0;

  onToggleSideNav(data: SideNavToggle): void {
    this.screenWidth = data.screenWidth;
    this.isSideNavCollapsed = data.collapsed;
  }
}
