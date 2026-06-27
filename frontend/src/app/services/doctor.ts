import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { getApiUrl } from '../env';

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
  private get API() { return `${getApiUrl()}/api/doctors`; }

  constructor(private http: HttpClient) {}

  getAll(): Observable<Doctor[]> {
    return this.http.get<Doctor[]>(this.API);
  }

  getById(id: number): Observable<Doctor> {
    return this.http.get<Doctor>(`${this.API}/${id}`);
  }

  getAvailableSlots(doctorId: number): Observable<TimeSlot[]> {
    return this.http.get<TimeSlot[]>(`${this.API}/${doctorId}/slots`);
  }

  addSlot(doctorId: number, slot: { startTime: string; endTime: string }): Observable<TimeSlot> {
    return this.http.post<TimeSlot>(`${this.API}/${doctorId}/slots`, slot);
  }
}
