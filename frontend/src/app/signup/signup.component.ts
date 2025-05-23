import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { SignupData } from '../models/user.model';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [FormsModule, CommonModule, ReactiveFormsModule],
  template: `
    <div class="container">
      <div class="signup-box">
        <h1>Inscription</h1>
        <form [formGroup]="signupForm" (ngSubmit)="onSubmit()" class="signup-form">
          <div class="form-group">
            <label for="username">Nom d'utilisateur</label>
            <input type="text" id="username" formControlName="username" placeholder="Entrez votre nom d'utilisateur">
            <div *ngIf="signupForm.get('username')?.invalid && signupForm.get('username')?.touched" class="error-message">
              Le nom d'utilisateur est requis
            </div>
          </div>
          <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" formControlName="email" placeholder="Entrez votre email">
            <div *ngIf="signupForm.get('email')?.invalid && signupForm.get('email')?.touched" class="error-message">
              Email invalide
            </div>
          </div>
          <div class="form-group">
            <label for="password">Mot de passe</label>
            <input type="password" id="password" formControlName="password" placeholder="Créez votre mot de passe">
            <div *ngIf="signupForm.get('password')?.invalid && signupForm.get('password')?.touched" class="error-message">
              Le mot de passe est requis
            </div>
          </div>
          <div class="form-group">
            <label for="confirmPassword">Confirmer le mot de passe</label>
            <input type="password" id="confirmPassword" formControlName="confirmPassword" placeholder="Confirmez votre mot de passe">
            <div *ngIf="signupForm.get('confirmPassword')?.invalid && signupForm.get('confirmPassword')?.touched" class="error-message">
              Les mots de passe ne correspondent pas
            </div>
          </div>
          <button type="submit" class="submit-btn" [disabled]="signupForm.invalid">S'inscrire</button>
          <p class="login-link">Déjà un compte ? <a (click)="navigateToLogin()">Se connecter</a></p>
        </form>
      </div>
    </div>
  `,
  styles: `
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.signup-box {
  background: #ffffff;
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  width: 100%;
  max-width: 420px;
  transition: transform 0.3s ease;
}

.signup-box:hover {
  transform: translateY(-5px);
}

h1 {
  color: #333;
  text-align: center;
  margin-bottom: 30px;
  font-size: 32px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 25px;
}

label {
  display: block;
  margin-bottom: 6px;
  color: #444;
  font-size: 15px;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 15px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.error-message {
  color: #e74c3c;
  font-size: 13px;
  margin-top: 5px;
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(to right, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s, transform 0.2s;
}

.submit-btn:disabled {
  background: #bbb;
  cursor: not-allowed;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  background: linear-gradient(to right, #5a67d8, #6b46c1);
}

.login-link {
  text-align: center;
  margin-top: 25px;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}

  `
})
export class SignupComponent {
  signupForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.signupForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    }, { validator: this.passwordMatchValidator });
  }

  passwordMatchValidator(g: FormGroup) {
    return g.get('password')?.value === g.get('confirmPassword')?.value
      ? null
      : { mismatch: true };
  }

  onSubmit() {
    if (this.signupForm.valid) {
      const userData: SignupData = {
        username: this.signupForm.value.username,
        email: this.signupForm.value.email,
        password: this.signupForm.value.password
      };

      this.authService.signup(userData).subscribe({
        next: (response) => {
          console.log('Inscription réussie', response);
          this.router.navigate(['/login']);
        },
        error: (error) => {
          console.error('Erreur lors de l\'inscription', error);
          // Ici vous pouvez ajouter la gestion des erreurs (affichage d'un message à l'utilisateur)
        }
      });
    }
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }
} 