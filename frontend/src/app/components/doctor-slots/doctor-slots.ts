import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule, DatePipe } from '@angular/common';
import { Subscription } from 'rxjs';
import { DoctorService, TimeSlot, Doctor } from '../../services/doctor';
import { AppointmentService } from '../../services/appointment';
import { WebsocketService } from '../../services/websocket';

@Component({
  selector: 'app-doctor-slots',
  imports: [CommonModule, DatePipe, MatCardModule, MatButtonModule, MatChipsModule, MatSnackBarModule],
  templateUrl: './doctor-slots.html',
  styles: `.slots { display:grid; grid-template-columns:repeat(auto-fill,minmax(220px,1fr)); gap:12px; padding:24px; } h2 { padding:0 24px; }`
})
export class DoctorSlotsComponent implements OnInit, OnDestroy {
  doctor?: Doctor;
  slots: TimeSlot[] = [];
  doctorId!: number;
  private wsSub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private doctorService: DoctorService,
    private appointmentService: AppointmentService,
    private wsService: WebsocketService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.doctorId = +this.route.snapshot.paramMap.get('id')!;
    this.doctorService.getById(this.doctorId).subscribe(d => this.doctor = d);
    this.loadSlots();

    this.wsService.connect();
    this.wsSub = this.wsService.subscribeToSlots(this.doctorId).subscribe(updatedSlot => {
      const idx = this.slots.findIndex(s => s.id === updatedSlot.id);
      if (idx >= 0) this.slots[idx] = updatedSlot;
      else this.slots.push(updatedSlot);
    });
  }

  ngOnDestroy() {
    this.wsSub?.unsubscribe();
  }

  loadSlots() {
    this.doctorService.getAvailableSlots(this.doctorId).subscribe(s => this.slots = s);
  }

  book(slot: TimeSlot) {
    this.appointmentService.markBeingReserved(slot.id).subscribe({
      next: () => {
        this.appointmentService.book(slot.id, '').subscribe({
          next: () => {
            this.snackBar.open('Wizyta zarezerwowana!', 'OK', { duration: 3000 });
            this.router.navigate(['/my-appointments']);
          },
          error: () => this.snackBar.open('Błąd rezerwacji', 'OK', { duration: 3000 })
        });
      },
      error: () => this.snackBar.open('Termin jest już zajęty', 'OK', { duration: 3000 })
    });
  }

  getStatusColor(status: string): string {
    return status === 'AVAILABLE' ? 'primary' : status === 'BEING_RESERVED' ? 'warn' : 'accent';
  }

  getStatusLabel(status: string): string {
    return status === 'AVAILABLE' ? 'Dostępny' : status === 'BEING_RESERVED' ? 'Rezerwowany...' : 'Zajęty';
  }
}
