import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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
  private readonly API = 'http://localhost:8080/api/appointments';

  constructor(private http: HttpClient) {}

  book(timeSlotId: number, notes: string): Observable<Appointment> {
    return this.http.post<Appointment>(this.API, { timeSlotId, notes });
  }

  getMyAppointments(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.API}/my`);
  }

  cancel(id: number): Observable<Appointment> {
    return this.http.put<Appointment>(`${this.API}/${id}/cancel`, {});
  }

  markBeingReserved(slotId: number): Observable<void> {
    return this.http.post<void>(`${this.API}/slots/${slotId}/reserve`, {});
  }
}
