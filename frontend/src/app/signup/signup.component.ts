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
    }

    .signup-box {
      background: white;
      padding: 40px;
      border-radius: 10px;
      box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
      width: 100%;
      max-width: 400px;
    }

    h1 {
      color: #333;
      text-align: center;
      margin-bottom: 30px;
      font-size: 28px;
    }

    .form-group {
      margin-bottom: 20px;
    }

    label {
      display: block;
      margin-bottom: 8px;
      color: #555;
      font-size: 14px;
    }

    input {
      width: 100%;
      padding: 12px;
      border: 1px solid #ddd;
      border-radius: 5px;
      font-size: 16px;
      transition: border-color 0.3s;
    }

    input:focus {
      outline: none;
      border-color: #667eea;
    }

    .error-message {
      color: #ff4444;
      font-size: 12px;
      margin-top: 5px;
    }

    .submit-btn {
      width: 100%;
      padding: 12px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border: none;
      border-radius: 5px;
      font-size: 16px;
      cursor: pointer;
      transition: transform 0.2s;
    }

    .submit-btn:disabled {
      background: #ccc;
      cursor: not-allowed;
    }

    .submit-btn:hover:not(:disabled) {
      transform: translateY(-2px);
    }

    .login-link {
      text-align: center;
      margin-top: 20px;
      color: #666;
      font-size: 14px;
    }

    .login-link a {
      color: #667eea;
      text-decoration: none;
      cursor: pointer;
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