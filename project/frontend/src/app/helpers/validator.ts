import {FormControl} from "@angular/forms";

export function minimalAge(brithDate: FormControl) {
  let timeDiff = Math.abs(Date.now() - new Date(brithDate.value).getTime());
  let age = Math.floor((timeDiff / (1000 * 3600 * 24)) / 365.25);
  if (age < 18) {
    return {
      underAge: true
    }
  }
  return null;
}
