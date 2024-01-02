import {Component, computed, EventEmitter, HostListener, inject, OnInit, Output} from '@angular/core';
import {navbarData} from "./nav-data";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {AuthenticationService} from "../services/authentication.service";
import {TranslateModule} from "@ngx-translate/core";

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
    TranslateModule
  ],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.scss'
})
export class SidenavComponent implements OnInit {
  @Output() onToggleSideNav: EventEmitter<SideNavToggle> = new EventEmitter();
  private auth = inject(AuthenticationService)
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
}
