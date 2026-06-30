import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AppointmentService, Appointment } from './appointment';

describe('AppointmentService', () => {
  let service: AppointmentService;
  let httpMock: HttpTestingController;

  const mockAppointment: Appointment = {
    id: 1,
    doctor: { id: 1, firstName: 'Jan', lastName: 'Kowalski' },
    patient: { id: 1, firstName: 'Piotr', lastName: 'Wiśniewski' },
    timeSlot: { id: 10, startTime: '2026-07-01T09:00:00', endTime: '2026-07-01T09:30:00' },
    status: 'SCHEDULED',
    notes: 'Kontrola',
    createdAt: '2026-06-30T10:00:00',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    const http = TestBed.inject(HttpClient);
    service = new AppointmentService(http);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());


  it('getMyAppointments() powinien zwrocic liste wizyt', () => {
    let result: Appointment[] = [];
    service.getMyAppointments().subscribe(a => (result = a));

    const req = httpMock.expectOne(r => r.url.includes('/api/appointments/my'));
    expect(req.request.method).toBe('GET');
    req.flush([mockAppointment]);

    expect(result.length).toBe(1);
    expect(result[0].notes).toBe('Kontrola');
  });

  it('cancel() powinien wyslac PUT i zwrocic wizyte ze statusem CANCELLED', () => {
    const cancelled = { ...mockAppointment, status: 'CANCELLED' as const };
    let result: Appointment | undefined;
    service.cancel(1).subscribe(a => (result = a));

    const req = httpMock.expectOne(r => r.url.includes('/api/appointments/1/cancel'));
    expect(req.request.method).toBe('PUT');
    req.flush(cancelled);

    expect(result?.status).toBe('CANCELLED');
  });

  it('markBeingReserved() powinien wyslac POST na endpoint reserve', () => {
    service.markBeingReserved(10).subscribe();

    const req = httpMock.expectOne(r => r.url.includes('/api/appointments/slots/10/reserve'));
    expect(req.request.method).toBe('POST');
    req.flush(null);
  });
});
