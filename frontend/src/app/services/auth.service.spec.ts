import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from './auth.service';
import { User, SignupData } from '../models/user.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule
      ],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login successfully', () => {
    const testToken = 'test-jwt-token';
    const email = 'test@example.com';
    const password = 'password123';

    service.login(email, password).subscribe(token => {
      expect(token).toBe(testToken);
      expect(service.getToken()).toBe(testToken);
      expect(service.isLoggedIn()).toBeTruthy();
    });

    const req = httpMock.expectOne('/pmt/users/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email, password });
    req.flush(testToken);
  });

  it('should handle signup successfully', () => {
    const signupData: SignupData = {
      username: 'johndoe',
      email: 'test@example.com',
      password: 'password123'
    };

    service.signup(signupData).subscribe(response => {
      expect(response.status).toBe(201);
    });

    const req = httpMock.expectOne('/pmt/users/signup');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(signupData);
    req.flush({}, { status: 201, statusText: 'Created' });
  });

  it('should logout correctly', () => {
    localStorage.setItem('jwt_token', 'test-token');
    localStorage.setItem('current_user', JSON.stringify({ id: 1, email: 'test@example.com', username: 'testuser' }));

    service.logout();

    expect(service.getToken()).toBeNull();
    expect(service.isLoggedIn()).toBeFalsy();
    expect(localStorage.getItem('current_user')).toBeNull();
  });

  it('should get auth headers with token', () => {
    const token = 'test-token';
    localStorage.setItem('jwt_token', token);

    const headers = service.getAuthHeaders();
    expect(headers.get('Authorization')).toBe(`Bearer ${token}`);
    expect(headers.get('Content-Type')).toBe('application/json');
    expect(headers.get('Accept')).toBe('application/json');
  });

  it('should get user info successfully', () => {
    const mockUser: User = {
      id: 1,
      email: 'test@example.com',
      username: 'testuser'
    };

    localStorage.setItem('jwt_token', 'test-token');

    service.getUserInfo().subscribe(user => {
      expect(user).toEqual(mockUser);
      expect(JSON.parse(localStorage.getItem('current_user') || '')).toEqual(mockUser);
    });

    const req = httpMock.expectOne('/pmt/users/me');
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
  });

  it('should handle 403 error in getUserInfo', () => {
    localStorage.setItem('jwt_token', 'test-token');

    service.getUserInfo().subscribe({
      error: (error) => {
        expect(error.message).toBe('Session expirée');
        expect(service.isLoggedIn()).toBeFalsy();
      }
    });

    const req = httpMock.expectOne('/pmt/users/me');
    req.flush('', { status: 403, statusText: 'Forbidden' });
  });
}); 