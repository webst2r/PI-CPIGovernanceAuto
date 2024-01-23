import {Component, computed, EventEmitter, HostListener, inject, OnInit, Output} from '@angular/core';
import {navbarData} from "./nav-data";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {AuthenticationService} from "../services/authentication.service";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {MatListModule} from "@angular/material/list";
import {StorageKey, StorageService} from "../services/storage.service";

interface SideNavToggle {
  screenWidth: number;
  collapsed: boolean;
}

@Component({
  selector: 'app-sidenav',
  standalone: true,
  imports: [
    RouterLink,
    NgIf,
    NgForOf,
    NgClass,
    RouterLinkActive,
    TranslateModule,
    MatListModule
  ],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.scss'
})
export class SidenavComponent implements OnInit {
  @Output() onToggleSideNav: EventEmitter<SideNavToggle> = new EventEmitter();
  private auth = inject(AuthenticationService)
  private translateService = inject(TranslateService)
  private storageService = inject(StorageService)
  collapsed = false;
  screenWidth = 0;

  navData = computed(() => {
    return navbarData.filter((data) =>
      data.authRequired === this.auth.isLoggedInSig())
  });

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.screenWidth = window.innerWidth;
    if (this.screenWidth <= 768) {
      this.collapsed = false;
      this.onToggleSideNav.emit({collapsed: this.collapsed, screenWidth: this.screenWidth});
    }
  }

  toggleCollapse(): void {
    this.collapsed = !this.collapsed;
    this.onToggleSideNav.emit({collapsed: this.collapsed, screenWidth: this.screenWidth});
  }

  closeSidenav(): void {
    this.collapsed = false;
    this.onToggleSideNav.emit({collapsed: this.collapsed, screenWidth: this.screenWidth});
  }

  ngOnInit(): void {
    this.screenWidth = window.innerWidth;
  }

  changeLanguage(lang: 'en' | 'pt') {
    this.translateService.use(lang);
    this.storageService.saveData(StorageKey.LANGUAGE, lang)
  }
}
