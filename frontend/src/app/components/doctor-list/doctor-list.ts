import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CommonModule } from '@angular/common';
import { DoctorService, Doctor } from '../../services/doctor';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-doctor-list',
  imports: [CommonModule, RouterLink, MatCardModule, MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './doctor-list.html',
  styles: `.grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(280px,1fr)); gap:16px; padding:24px; } h2 { padding:0 24px; }`
})
export class DoctorListComponent implements OnInit {
  doctors: Doctor[] = [];
  loading = true;

  constructor(private doctorService: DoctorService, public auth: AuthService) {}

  ngOnInit() {
    this.doctorService.getAll().subscribe({
      next: (data) => { this.doctors = data; this.loading = false; },
      error: () => this.loading = false
    });
  }
}
