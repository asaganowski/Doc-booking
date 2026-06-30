import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { DoctorService, Doctor, TimeSlot } from './doctor';

describe('DoctorService', () => {
  let service: DoctorService;
  let httpMock: HttpTestingController;

  const mockDoctors: Doctor[] = [
    { id: 1, firstName: 'Jan', lastName: 'Kowalski', specialization: 'Kardiologia', phoneNumber: '111222333' },
    { id: 2, firstName: 'Anna', lastName: 'Nowak', specialization: 'Pediatria', phoneNumber: '444555666' },
  ];

  const mockSlots: TimeSlot[] = [
    { id: 10, startTime: '2026-07-01T09:00:00', endTime: '2026-07-01T09:30:00', status: 'AVAILABLE', doctor: mockDoctors[0] },
    { id: 11, startTime: '2026-07-01T10:00:00', endTime: '2026-07-01T10:30:00', status: 'BOOKED', doctor: mockDoctors[0] },
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    const http = TestBed.inject(HttpClient);
    service = new DoctorService(http);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());


  it('getById() powinien zwrocic lekarza po id', () => {
    let result: Doctor | undefined;
    service.getById(1).subscribe(d => (result = d));

    const req = httpMock.expectOne(r => r.url.includes('/api/doctors/1'));
    expect(req.request.method).toBe('GET');
    req.flush(mockDoctors[0]);

    expect(result?.lastName).toBe('Kowalski');
  });

  it('getAvailableSlots() powinien zwrocic sloty lekarza', () => {
    let result: TimeSlot[] = [];
    service.getAvailableSlots(1).subscribe(s => (result = s));

    const req = httpMock.expectOne(r => r.url.includes('/api/doctors/1/slots'));
    expect(req.request.method).toBe('GET');
    req.flush(mockSlots);

    expect(result.length).toBe(2);
    expect(result[0].status).toBe('AVAILABLE');
  });

  it('addSlot() powinien wyslac POST z danymi slotu', () => {
    const newSlot = { startTime: '2026-07-02T09:00:00', endTime: '2026-07-02T09:30:00' };
    service.addSlot(1, newSlot).subscribe();

    const req = httpMock.expectOne(r => r.url.includes('/api/doctors/1/slots'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newSlot);
    req.flush({ id: 12, ...newSlot, status: 'AVAILABLE', doctor: mockDoctors[0] });
  });
});
