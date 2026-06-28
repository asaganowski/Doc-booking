import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Doctor {
  id: number;
  firstName: string;
  lastName: string;
  specialization: string;
  phoneNumber: string;
}

export interface TimeSlot {
  id: number;
  startTime: string;
  endTime: string;
  status: 'AVAILABLE' | 'BEING_RESERVED' | 'BOOKED';
  doctor: Doctor;
}

@Injectable({ providedIn: 'root' })
export class DoctorService {
  private baseUrl = `${environment.apiUrl}/api/doctors`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Doctor[]> {
    return this.http.get<Doctor[]>(this.baseUrl);
  }

  getById(id: number): Observable<Doctor> {
    return this.http.get<Doctor>(`${this.baseUrl}/${id}`);
  }

  getAvailableSlots(doctorId: number): Observable<TimeSlot[]> {
    return this.http.get<TimeSlot[]>(`${this.baseUrl}/${doctorId}/slots`);
  }

  addSlot(doctorId: number, slot: { startTime: string; endTime: string }): Observable<TimeSlot> {
    return this.http.post<TimeSlot>(`${this.baseUrl}/${doctorId}/slots`, slot);
  }
}
