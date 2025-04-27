import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project, ProjectDto } from '../models/project.model';
import { AuthService } from './auth.service';
import { EmailRole } from '../models/email-role.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private baseUrl = '/pmt/projects';
  private headers: HttpHeaders;

  constructor(private http: HttpClient, private authService: AuthService) {
    this.headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
  }

  inviteMembers(projectId: number, emailRoles: EmailRole[]): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<void>(`${this.baseUrl}/invite/${projectId}`, emailRoles, { headers });
  }

  getProjects(): Observable<Project[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Project[]>(this.baseUrl, { headers });
  }

  createProject(projectDto: ProjectDto): Observable<Project> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<Project>(`${this.baseUrl}/create`, projectDto, { headers });
  }

  deleteProject(id: number): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers });
  }
} 