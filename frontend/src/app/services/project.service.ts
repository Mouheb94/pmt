import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, Subject, tap } from 'rxjs';
import { Project, ProjectDto, Task } from '../models/project.model';
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
  private projectCreatedSource = new Subject<void>();
  projectCreated$ = this.projectCreatedSource.asObservable()

  inviteMembers(projectId: number, emailRoles: EmailRole[]): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<void>(`${this.baseUrl}/invite/${projectId}`, emailRoles, { headers });
  }

  getProjects(): Observable<Project[]> {
    const headers = this.authService.getAuthHeaders();
    console.log('Envoi de la requête GET pour récupérer les projets');
    return this.http.get<Project[]>(`${this.baseUrl}/details`, { headers }).pipe(
      tap({
        next: (projects) => console.log('Réponse du serveur pour getProjects:', projects),
        error: (error) => console.error('Erreur serveur pour getProjects:', error)
      })
    );
  }

  getProjectDetails(id: number): Observable<Project> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Project>(`${this.baseUrl}/${id}`, { headers });
  }

  createProject(projectDto: ProjectDto): Observable<Project> {
    const headers = this.authService.getAuthHeaders();
    console.log('Envoi de la requête POST pour créer le projet:', projectDto);
    return this.http.post<Project>(`${this.baseUrl}/create`, projectDto, { headers }).pipe(
      tap({
        next: (project) => {
          console.log('Réponse du serveur pour createProject:', project);
          this.projectCreatedSource.next();
        },
        error: (error) => console.error('Erreur serveur pour createProject:', error)
      })
    );
  }

  deleteProject(id: number): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers });
  }

  createTask(projectId: number, taskData: Partial<Task>): Observable<Task> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<Task>(`${this.baseUrl}/${projectId}/tasks`, taskData, { headers });
  }
} 