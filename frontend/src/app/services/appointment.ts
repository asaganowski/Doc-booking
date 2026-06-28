import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Appointment {
  id: number;
  doctor: any;
  patient: any;
  timeSlot: any;
  status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED';
  notes: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class AppointmentService {
  private baseUrl = `${environment.apiUrl}/api/appointments`;

  constructor(private http: HttpClient) {}

  book(timeSlotId: number, notes: string): Observable<Appointment> {
    return this.http.post<Appointment>(this.baseUrl, { timeSlotId, notes });
  }

  getMyAppointments(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.baseUrl}/my`);
  }

  cancel(id: number): Observable<Appointment> {
    return this.http.put<Appointment>(`${this.baseUrl}/${id}/cancel`, {});
  }

  markBeingReserved(slotId: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/slots/${slotId}/reserve`, {});
  }
}
