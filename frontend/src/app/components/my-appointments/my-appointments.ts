import { Component, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule, DatePipe } from '@angular/common';
import { AppointmentService, Appointment } from '../../services/appointment';

@Component({
  selector: 'app-my-appointments',
  imports: [CommonModule, DatePipe, MatCardModule, MatButtonModule, MatChipsModule, MatSnackBarModule],
  templateUrl: './my-appointments.html',
  styles: `.grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(280px,1fr)); gap:16px; padding:24px; } h2 { padding:0 24px; }`
})
export class MyAppointmentsComponent implements OnInit {
  appointments: Appointment[] = [];

  constructor(private appointmentService: AppointmentService, private snackBar: MatSnackBar) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.appointmentService.getMyAppointments().subscribe(a => this.appointments = a);
  }

  cancel(id: number) {
    this.appointmentService.cancel(id).subscribe({
      next: () => {
        this.snackBar.open('Wizyta anulowana', 'OK', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Błąd anulowania', 'OK', { duration: 3000 })
    });
  }

  getStatusColor(status: string): string {
    return status === 'SCHEDULED' ? 'primary' : status === 'COMPLETED' ? 'accent' : 'warn';
  }

  getStatusLabel(status: string): string {
    return status === 'SCHEDULED' ? 'Zaplanowana' : status === 'COMPLETED' ? 'Zakończona' : 'Anulowana';
  }
}
