import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectService } from './project.service';
import { AuthService } from './auth.service';
import { Project, ProjectDto, Task } from '../models/project.model';
import { EmailRole } from '../models/email-role.model';
import { ProjectRole } from '../models/role.model';

describe('ProjectService', () => {
  let service: ProjectService;
  let httpMock: HttpTestingController;
  let authService: jasmine.SpyObj<AuthService>;

  const mockProject: Project = {
    id: 1,
    name: 'Test Project',
    description: 'Test Description',
    startDate: new Date().toISOString(),
    createdBy: {
      id: 1,
      username: 'testuser'
    },
    members: [],
    tasks: []
  };

  const mockProjectDto: ProjectDto = {
    name: 'New Project',
    description: 'New Description',
    startDate: new Date()
  };

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
        ProjectService,
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });

    service = TestBed.inject(ProjectService);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should invite members to a project', () => {
    const projectId = 1;
    const emailRoles: EmailRole[] = [
      { email: 'test@example.com', role: ProjectRole.MEMBER }
    ];

    service.inviteMembers(projectId, emailRoles).subscribe(response => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne(`/pmt/projects/invite/${projectId}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(emailRoles);
    req.flush(null);
  });

  it('should get all projects', () => {
    const mockProjects = [mockProject];

    service.getProjects().subscribe(projects => {
      expect(projects).toEqual(mockProjects);
    });

    const req = httpMock.expectOne('/pmt/projects/details');
    expect(req.request.method).toBe('GET');
    req.flush(mockProjects);
  });

  it('should get project details', () => {
    const projectId = 1;

    service.getProjectDetails(projectId).subscribe(project => {
      expect(project).toEqual(mockProject);
    });

    const req = httpMock.expectOne(`/pmt/projects/${projectId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProject);
  });

  it('should create a project', () => {
    service.createProject(mockProjectDto).subscribe(project => {
      expect(project).toEqual(mockProject);
    });

    const req = httpMock.expectOne('/pmt/projects/create');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockProjectDto);
    req.flush(mockProject);
  });

  it('should delete a project', () => {
    const projectId = 1;

    service.deleteProject(projectId).subscribe(response => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne(`/pmt/projects/${projectId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should create a task in a project', () => {
    const projectId = 1;
    const taskData: Partial<Task> = {
      name: 'New Task',
      description: 'New Description'
    };

    service.createTask(projectId, taskData).subscribe(task => {
      expect(task).toEqual(mockTask);
    });

    const req = httpMock.expectOne(`/pmt/projects/${projectId}/tasks`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(taskData);
    req.flush(mockTask);
  });
}); 