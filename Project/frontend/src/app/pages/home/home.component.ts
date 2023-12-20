import { Component } from '@angular/core';
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatToolbarModule} from "@angular/material/toolbar";
import {SidenavComponent} from "../sidenav/sidenav.component";
import {BodyComponent} from "../body/body.component";
import {RouterOutlet} from "@angular/router";
import {FlowsComponent} from "../flows/flows.component";

interface SideNavToggle {
  screenWidth: number,
  collapsed: boolean;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    MatSidenavModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    SidenavComponent,
    BodyComponent,
    RouterOutlet,
    FlowsComponent
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {

  isSideNavCollapsed = false;
  screenWidth = 0;
  pageType = "home";
  onToggleSideNav(data: SideNavToggle): void {
    this.screenWidth = data.screenWidth;
    this.isSideNavCollapsed = data.collapsed;
  }
}
