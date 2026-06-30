import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { authInterceptor } from './auth-interceptor';
import { AuthService } from '../services/auth';
import { Router } from '@angular/router';

describe('authInterceptor', () => {
  let httpMock: HttpTestingController;
  let http: HttpClient;
  let authService: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    });
    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    const router = TestBed.inject(Router);
    authService = new AuthService(TestBed.inject(HttpClient), router);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('powinien dodac naglowek Authorization gdy token istnieje', () => {
    localStorage.setItem('token', 'test.jwt.token');

    http.get('/api/doctors').subscribe();

    const req = httpMock.expectOne('/api/doctors');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test.jwt.token');
    req.flush([]);
  });

  it('NIE powinien dodawac naglowka Authorization gdy brak tokenu', () => {
    localStorage.removeItem('token');

    http.get('/api/doctors').subscribe();

    const req = httpMock.expectOne('/api/doctors');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush([]);
  });
});
