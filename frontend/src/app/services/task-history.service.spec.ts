import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskHistoryService, TaskHistory } from './task-history.service';
import { AuthService } from './auth.service';

describe('TaskHistoryService', () => {
  let service: TaskHistoryService;
  let httpMock: HttpTestingController;
  let authService: jasmine.SpyObj<AuthService>;

  const mockTaskHistory: TaskHistory[] = [
    {
      id: 1,
      taskId: 1,
      modifiedBy: 'testuser',
      modificationDate: '2024-05-11T12:00:00Z',
      fieldChanged: 'status',
      oldValue: 'TODO',
      newValue: 'IN_PROGRESS'
    },
    {
      id: 2,
      taskId: 1,
      modifiedBy: 'testuser',
      modificationDate: '2024-05-11T13:00:00Z',
      fieldChanged: 'assignedTo',
      oldValue: 'null',
      newValue: 'user1'
    }
  ];

  beforeEach(() => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getAuthHeaders']);
    authServiceSpy.getAuthHeaders.and.returnValue({
      get: () => 'Bearer test-token'
    });

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        TaskHistoryService,
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });

    service = TestBed.inject(TaskHistoryService);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get task history', () => {
    const taskId = 1;

    service.getTaskHistory(taskId).subscribe(history => {
      expect(history).toEqual(mockTaskHistory);
    });

    const req = httpMock.expectOne(`/pmt/task-history/${taskId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockTaskHistory);
  });
}); 