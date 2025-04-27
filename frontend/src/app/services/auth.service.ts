import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, catchError, throwError, of } from 'rxjs';
import { tap, switchMap, map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { User, SignupData } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = '/pmt/users';
  private tokenKey = 'jwt_token';
  private userKey = 'current_user';
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.checkAuthStatus();
  }

  private checkAuthStatus(): void {
    const token = this.getToken();
    this.isAuthenticatedSubject.next(!!token);
  }

  login(email: string, password: string): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}/login`, {email, password}, { 
      responseType: 'text' as 'json'
    }).pipe(
      tap(token => {
        this.setToken(token);
        this.isAuthenticatedSubject.next(true);
      })
    );
  }

  handleLoginSuccess(token: string): void {
    this.setToken(token);
    this.isAuthenticatedSubject.next(true);
    this.router.navigate(['/home']);
  }

  signup(userData: SignupData): Observable<HttpResponse<any>> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });

    return this.http.post<HttpResponse<any>>(`${this.baseUrl}/signup`, userData, { 
      headers,
      observe: 'response'
    });
  }

  private setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.isAuthenticatedSubject.next(false);
  }

  isLoggedIn(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  getCurrentUser(): User | null {
    const userStr = localStorage.getItem(this.userKey);
    return userStr ? JSON.parse(userStr) : null;
  }

  getUserInfo(): Observable<User> {
    const headers = this.getAuthHeaders();
    return this.http.get<User>(`${this.baseUrl}/me`, { headers }).pipe(
      tap(user => {
        localStorage.setItem(this.userKey, JSON.stringify(user));
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Erreur lors de la récupération des informations utilisateur:', error);
        if (error.status === 403) {
          this.logout(); // Déconnexion si le token n'est pas valide
          return throwError(() => new Error('Session expirée'));
        }
        return throwError(() => error);
      })
    );
  }
} 