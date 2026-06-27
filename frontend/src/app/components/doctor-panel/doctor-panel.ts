import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { DoctorService, TimeSlot, Doctor } from '../../services/doctor';
import { AuthService } from '../../services/auth';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-doctor-panel',
  imports: [
    CommonModule, DatePipe, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatSnackBarModule, MatDividerModule, MatChipsModule
  ],
  templateUrl: './doctor-panel.html',
  styles: `.layout { display:grid; grid-template-columns:1fr 1fr; gap:24px; padding:24px; }
           h2 { padding:0 24px; }
           mat-card-content { display:flex; flex-direction:column; gap:12px; }
           .slots-list { display:flex; flex-direction:column; gap:8px; max-height:500px; overflow-y:auto; }`
})
export class DoctorPanelComponent implements OnInit {
  form;
  slots: TimeSlot[] = [];
  doctor?: Doctor;
  doctorId?: number;

  constructor(
    private fb: FormBuilder,
    private doctorService: DoctorService,
    private auth: AuthService,
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      startTime: ['', Validators.required],
      endTime: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.http.get<any>('http://localhost:8080/api/doctors/me').subscribe({
      next: (doctor) => {
        this.doctor = doctor;
        this.doctorId = doctor.id;
        this.loadSlots();
      },
      error: () => this.snackBar.open('Nie można załadować profilu lekarza', 'OK', { duration: 3000 })
    });
  }

  loadSlots() {
    if (!this.doctorId) return;
    this.http.get<TimeSlot[]>(`http://localhost:8080/api/doctors/${this.doctorId}/slots/all`).subscribe(
      s => this.slots = s.sort((a, b) => a.startTime.localeCompare(b.startTime))
    );
  }

  addSlot() {
    if (this.form.invalid || !this.doctorId) return;
    const { startTime, endTime } = this.form.value;
    this.doctorService.addSlot(this.doctorId, {
      startTime: startTime!,
      endTime: endTime!
    }).subscribe({
      next: () => {
        this.snackBar.open('Termin dodany!', 'OK', { duration: 3000 });
        this.form.reset();
        this.loadSlots();
      },
      error: () => this.snackBar.open('Błąd dodawania terminu', 'OK', { duration: 3000 })
    });
  }

  getStatusColor(status: string): string {
    return status === 'AVAILABLE' ? 'primary' : status === 'BEING_RESERVED' ? 'warn' : 'accent';
  }

  getStatusLabel(status: string): string {
    return status === 'AVAILABLE' ? 'Dostępny' : status === 'BEING_RESERVED' ? 'Rezerwowany' : 'Zajęty';
  }
}
