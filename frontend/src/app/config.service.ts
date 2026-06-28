import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ConfigService {
  apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  load(): Promise<void> {
    return firstValueFrom(
      this.http.get<{ apiUrl: string }>('/assets/env.json')
    ).then(config => {
      this.apiUrl = config.apiUrl;
    }).catch(() => {});
  }
}
