import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    });
    const http = TestBed.inject(HttpClient);
    const router = TestBed.inject(Router);
    service = new AuthService(http, router);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('login() powinien zapisac token i ustawic isLoggedIn na true', () => {
    service.login('testuser', 'haslo123').subscribe();

    const req = httpMock.expectOne(r => r.url.includes('/auth/login'));
    expect(req.request.method).toBe('POST');
    req.flush({ token: 'fake.jwt.token', username: 'testuser', role: 'PATIENT' });

    expect(localStorage.getItem('token')).toBe('fake.jwt.token');
    expect(service.isLoggedIn()).toBeTrue();
    expect(service.currentRole()).toBe('PATIENT');
  });

  it('logout() powinien usunac token i ustawic isLoggedIn na false', () => {
    localStorage.setItem('token', 'fake.jwt.token');
    localStorage.setItem('role', 'PATIENT');
    localStorage.setItem('username', 'testuser');

    service.logout();

    expect(localStorage.getItem('token')).toBeNull();
    expect(service.isLoggedIn()).toBeFalse();
    expect(service.currentRole()).toBeNull();
  });

  it('register() powinien zapisac token po rejestracji', () => {
    const registerData = {
      username: 'newuser',
      email: 'new@example.com',
      password: 'haslo123',
      firstName: 'Jan',
      lastName: 'Kowalski',
    };

    service.register(registerData).subscribe();

    const req = httpMock.expectOne(r => r.url.includes('/auth/register'));
    expect(req.request.method).toBe('POST');
    req.flush({ token: 'new.jwt.token', username: 'newuser', role: 'PATIENT' });

    expect(localStorage.getItem('token')).toBe('new.jwt.token');
    expect(service.isLoggedIn()).toBeTrue();
  });
});
