import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {BrowserAnimationsModule, provideNoopAnimations} from '@angular/platform-browser/animations';
import {HttpClient, provideHttpClient, withInterceptors} from "@angular/common/http";
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {TranslateHttpLoader} from "@ngx-translate/http-loader";
import {JwtModule} from "@auth0/angular-jwt";
import {StorageKey} from "./services/storage.service";
import {authenticatorInterceptor} from "./helpers/authenticator.interceptor";

// required for AoT
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}

export function tokenGetter() {
  return localStorage.getItem(StorageKey.TOKEN);
}

export const appConfig: ApplicationConfig = {
  providers: [provideRouter(routes),
    provideNoopAnimations(),
    provideHttpClient( withInterceptors([authenticatorInterceptor])),
    TranslateModule.forRoot({
      defaultLanguage: 'en',
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }).providers!,
    importProvidersFrom(
      JwtModule.forRoot({
        config: {
          tokenGetter: () => tokenGetter(),
          allowedDomains: ['localhost:4200/login', 'localhost:4200/register'],
          disallowedRoutes: [],
        },
      }),
    ),
    importProvidersFrom([BrowserAnimationsModule])
  ]
};
