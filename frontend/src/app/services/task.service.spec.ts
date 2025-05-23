import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService } from './task.service';
import { AuthService } from './auth.service';
import { Task } from '../models/project.model';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;
  let authService: jasmine.SpyObj<AuthService>;

  const mockTask: Task = {
    id: 1,
    name: 'Test Task',
    description: 'Test Description',
    status: 'TODO',
    priority: 'HIGH',
    dueDate: new Date().toISOString(),
    createdBy: {
      id: 1,
      username: 'testuser'
    },
    assignedTo: {
      id: 1,
      username: 'testuser'
    }
  };

  beforeEach(() => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getAuthHeaders']);
    authServiceSpy.getAuthHeaders.and.returnValue({
      get: () => 'Bearer test-token'
    });

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        TaskService,
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });

    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get tasks by project', () => {
    const projectId = 1;
    const mockTasks = [mockTask];

    service.getTasksByProject(projectId).subscribe(tasks => {
      expect(tasks).toEqual([{
        ...mockTask,
        assignedToId: mockTask.assignedTo?.id,
        assignedToUsername: mockTask.assignedTo?.username
      }]);
    });

    const req = httpMock.expectOne(`/pmt/tasks/project/${projectId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockTasks);
  });

  it('should create a task', () => {
    const projectId = 1;
    const taskData: Partial<Task> = {
      name: 'New Task',
      description: 'New Description'
    };

    service.createTask(projectId, taskData).subscribe(task => {
      expect(task).toEqual(mockTask);
    });

    const req = httpMock.expectOne(`/pmt/tasks/create/${projectId}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(taskData);
    req.flush(mockTask);
  });

  it('should update a task', () => {
    const taskId = 1;
    const currentUserId = 1;
    const taskData: Partial<Task> = {
      name: 'Updated Task',
      description: 'Updated Description'
    };

    service.updateTask(taskId, taskData, currentUserId).subscribe(task => {
      expect(task).toEqual(mockTask);
    });

    const req = httpMock.expectOne(`/pmt/tasks/${taskId}/${currentUserId}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(taskData);
    req.flush(mockTask);
  });

  it('should delete a task', () => {
    const taskId = 1;

    service.deleteTask(taskId).subscribe(response => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne(`/pmt/tasks/${taskId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should update task status', () => {
    const taskId = 1;
    const newStatus = 'IN_PROGRESS';

    service.updateTaskStatus(taskId, newStatus).subscribe(task => {
      expect(task).toEqual({
        ...mockTask,
        assignedToId: mockTask.assignedTo?.id,
        assignedToUsername: mockTask.assignedTo?.username
      });
    });

    const req = httpMock.expectOne(`/pmt/tasks/${taskId}/status`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ status: newStatus });
    req.flush(mockTask);
  });

  it('should assign a task', () => {
    const taskId = 1;
    const userId = 1;

    service.assignTask(taskId, userId).subscribe(task => {
      expect(task).toEqual(mockTask);
    });

    const req = httpMock.expectOne(`/pmt/tasks/assigner/${userId}/${taskId}`);
    expect(req.request.method).toBe('PATCH');
    req.flush(mockTask);
  });
}); 