declare global {
  interface Window {
    __env: { apiUrl: string };
  }
}

export function getApiUrl(): string {
  return window.__env?.apiUrl ?? 'http://localhost:8080';
}
