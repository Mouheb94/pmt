import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task } from '../models/project.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private baseUrl = '/pmt/tasks';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getTasksByProject(projectId: number): Observable<Task[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Task[]>(`${this.baseUrl}/project/${projectId}`, { headers });
  }

  createTask(projectId: number, taskData: Partial<Task>): Observable<Task> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<Task>(`${this.baseUrl}/create/${projectId}`, taskData, { headers });
  }

  updateTask(taskId: number, taskData: Partial<Task>): Observable<Task> {
    const headers = this.authService.getAuthHeaders();
    return this.http.put<Task>(`${this.baseUrl}/${taskId}`, taskData, { headers });
  }

  deleteTask(taskId: number): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<void>(`${this.baseUrl}/${taskId}`, { headers });
  }

  updateTaskStatus(taskId: number, status: string): Observable<Task> {
    const headers = this.authService.getAuthHeaders();
    return this.http.patch<Task>(`${this.baseUrl}/${taskId}/status`, { status }, { headers });
  }

  assignTask(taskId: number, userId: number | null): Observable<Task> {
    const headers = this.authService.getAuthHeaders();
    return this.http.patch<Task>(`${this.baseUrl}/${taskId}/assign`, { userId }, { headers });
  }
} 