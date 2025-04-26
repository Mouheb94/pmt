import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface Project {
  id: number;
  name: string;
  description: string;
  startDate: string;
  createdBy: {
    id: number;
    username: string;
  };
  members: {
    id: number;
    user: {
      id: number;
      username: string;
    };
    role: string;
  }[];
  tasks: {
    id: number;
    name: string;
    description: string;
    dueDate: string;
    priority: string;
    status: string;
    assignedTo: {
      id: number;
      username: string;
    } | null;
  }[];
}

export interface ProjectDto {
  id?: number;
  name: string;
  description: string;
  startDate: Date;
}

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private baseUrl = '/pmt/projects';

  constructor(private http: HttpClient, private authService: AuthService) { }

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