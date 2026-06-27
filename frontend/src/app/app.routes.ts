import { Routes } from '@angular/router';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: '', redirectTo: '/doctors', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./components/login/login').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./components/register/register').then(m => m.RegisterComponent) },
  { path: 'doctors', loadComponent: () => import('./components/doctor-list/doctor-list').then(m => m.DoctorListComponent), canActivate: [authGuard] },
  { path: 'doctors/:id/slots', loadComponent: () => import('./components/doctor-slots/doctor-slots').then(m => m.DoctorSlotsComponent), canActivate: [authGuard] },
  { path: 'my-appointments', loadComponent: () => import('./components/my-appointments/my-appointments').then(m => m.MyAppointmentsComponent), canActivate: [authGuard] },
  { path: 'doctor-panel', loadComponent: () => import('./components/doctor-panel/doctor-panel').then(m => m.DoctorPanelComponent), canActivate: [authGuard] },
  { path: '**', redirectTo: '/doctors' }
];
