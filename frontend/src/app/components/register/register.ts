import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterLink, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './register.html',
  styles: `.container { display:flex; justify-content:center; padding:40px; } mat-card { width:400px; } mat-card-content { display:flex; flex-direction:column; gap:16px; }`
})
export class RegisterComponent {
  form;
  error = '';

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phoneNumber: ['']
    });
  }

  submit() {
    if (this.form.invalid) return;
    this.auth.register({ ...this.form.value, role: 'PATIENT' }).subscribe({
      next: () => this.router.navigate(['/doctors']),
      error: (e) => this.error = e.error?.message || 'Błąd rejestracji'
    });
  }
}
