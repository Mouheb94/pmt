import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface TaskHistory {
  id: number;
  taskId: number;
  modifiedBy: string;
  modificationDate: string;
  fieldChanged: string;
  oldValue: string;
  newValue: string;
}

@Injectable({
  providedIn: 'root'
})
export class TaskHistoryService {
  private baseUrl = '/pmt/task-history';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getTaskHistory(taskId: number): Observable<TaskHistory[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<TaskHistory[]>(`${this.baseUrl}/${taskId}`, { headers });
  }
} 