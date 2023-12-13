import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import {HttpClient, provideHttpClient, withInterceptorsFromDi} from "@angular/common/http";
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {TranslateHttpLoader} from "@ngx-translate/http-loader";
import {JwtModule} from "@auth0/angular-jwt";
import {StorageKey} from "./services/storage.service";
// required for AoT
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}

export const appConfig: ApplicationConfig = {
  providers: [provideRouter(routes),
    provideNoopAnimations(),
    provideHttpClient( withInterceptorsFromDi()),

    TranslateModule.forRoot({
      defaultLanguage: 'pt',
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }).providers!,
    importProvidersFrom(
      JwtModule.forRoot({
        config: {
          tokenGetter: () => localStorage.getItem(StorageKey.TOKEN),
          allowedDomains: ['localhost:4200/login', 'localhost:4200/register'],
          disallowedRoutes: [],
        },
      }),
    ),
  ]
};
