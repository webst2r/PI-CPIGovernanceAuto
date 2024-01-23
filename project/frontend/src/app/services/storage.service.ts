import { Injectable } from '@angular/core';


export enum StorageKey{
  TOKEN='token',
  USER = 'user',
  LANGUAGE='lang'
}

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  public saveData(key: StorageKey, value: string) {
    localStorage.setItem(key, value);
  }

  public getData(key: StorageKey) {
    return localStorage.getItem(key)
  }
  public removeData(key: StorageKey) {
    localStorage.removeItem(key);
  }

  public clearData() {
    localStorage.clear();
  }
}
