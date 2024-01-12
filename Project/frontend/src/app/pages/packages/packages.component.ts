import { Component, OnInit } from '@angular/core';
import { PackagesService } from '../../services/packages.service';
import { Router } from '@angular/router';
import {MatTableModule} from "@angular/material/table";
import {TranslateModule} from "@ngx-translate/core";
import {PackagesElement} from "../../models/packages";


@Component({
  selector: 'app-packages',
  standalone: true,
  templateUrl: './packages.component.html',
  styleUrls: ['./packages.component.scss'],
  imports: [
    MatTableModule,
    TranslateModule
  ]
})
export class PackagesComponent implements OnInit {
  displayedColumns: string[] = ['position', 'name', 'version', 'modifiedBy', 'modifiedDate', 'actions'];
  dataSource: PackagesElement[] = [];

  constructor(private packagesService: PackagesService, private router: Router) {}

  ngOnInit() {
    this.getPackages();
  }

  getPackages() {
    this.packagesService.getPackages().subscribe((packages) => {
      this.dataSource = packages;
      console.log('Packages fetched successfully:', packages);
    });
  }

  openPackage(element: PackagesElement) {
    this.router.navigate(['/package-detail', element.name]);
  }
}
