import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HomeComponent } from './home.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ProjectService } from '../../services/project.service';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { Project, ProjectDto } from '../../models/project.model';
import { User } from '../../models/user.model';
import { ProjectRole } from '../../models/role.model';
import { InviteModalComponent } from './invite-modal/invite-modal.component';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let projectServiceSpy: jasmine.SpyObj<ProjectService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockUser: User = {
    id: 1,
    email: 'test@example.com',
    username: 'testuser'
  };

  const mockProjects: Project[] = [
    {
      id: 1,
      name: 'Test Project',
      description: 'Test Description',
      startDate: '2024-03-20',
      createdBy: mockUser,
      members: [
        { 
          userId: 1, 
          username: 'testuser',
          role: ProjectRole.ADMIN 
        }
      ],
      tasks: []
    }
  ];

  beforeEach(async () => {
    const projectSpy = jasmine.createSpyObj('ProjectService', [
      'getProjects',
      'createProject',
      'deleteProject',
      'projectCreated$'
    ]);
    const authSpy = jasmine.createSpyObj('AuthService', [
      'getCurrentUser',
      'getUserInfo',
      'logout'
    ]);

    // Simulate projectCreated$ as an Observable
    Object.defineProperty(projectSpy, 'projectCreated$', { get: () => of() });

    await TestBed.configureTestingModule({
      imports: [
        HomeComponent,
        RouterTestingModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: ProjectService, useValue: projectSpy },
        { provide: AuthService, useValue: authSpy }
      ]
    }).compileComponents();

    projectServiceSpy = TestBed.inject(ProjectService) as jasmine.SpyObj<ProjectService>;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;

    // Setup default spy behaviors
    projectServiceSpy.getProjects.and.returnValue(of(mockProjects));
    authServiceSpy.getCurrentUser.and.returnValue(mockUser);
    authServiceSpy.getUserInfo.and.returnValue(of(mockUser));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load projects on init', () => {
    fixture.detectChanges();
    expect(projectServiceSpy.getProjects).toHaveBeenCalled();
    expect(component.projects).toEqual(mockProjects);
  });

  it('should load current user on init', () => {
    fixture.detectChanges();
    expect(authServiceSpy.getCurrentUser).toHaveBeenCalled();
    expect(component.currentUser).toEqual(mockUser);
  });

  it('should check if user is project admin', () => {
    fixture.detectChanges();
    const isAdmin = component.isProjectAdmin(mockProjects[0]);
    expect(isAdmin).toBeTrue();
  });

  it('should create new project when form is valid', () => {
    const newProject: ProjectDto = {
      name: 'New Project',
      description: 'New Description',
      startDate: new Date('2024-03-20')
    };

    const createdProject: Project = {
      ...newProject,
      id: 2,
      createdBy: mockUser,
      members: [],
      tasks: [],
      startDate: newProject.startDate.toISOString().split('T')[0]
    };

    projectServiceSpy.createProject.and.returnValue(of(createdProject));

    component.projectForm.setValue({
      name: newProject.name,
      description: newProject.description,
      startDate: newProject.startDate.toISOString().split('T')[0]
    });

    component.onSubmit();

    expect(projectServiceSpy.createProject).toHaveBeenCalledWith({
      name: newProject.name,
      description: newProject.description,
      startDate: jasmine.any(Date)
    });
  });

  it('should delete project when confirmed', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    projectServiceSpy.deleteProject.and.returnValue(of(void 0));

    component.deleteProject(1);

    expect(projectServiceSpy.deleteProject).toHaveBeenCalledWith(1);
    expect(component.projects.length).toBe(0);
  });

  it('should handle project creation error', () => {
    const error = new Error('Test error');
    projectServiceSpy.createProject.and.returnValue(throwError(() => error));
    spyOn(console, 'error');

    component.projectForm.setValue({
      name: 'Test Project',
      description: 'Test Description',
      startDate: '2024-03-20'  // aussi une string ici
    });

    component.onSubmit();

    expect(console.error).toHaveBeenCalled();
  });

  it('should handle project loading error', () => {
    const error = new Error('Test error');
    projectServiceSpy.getProjects.and.returnValue(throwError(() => error));
    spyOn(console, 'error');

    component.loadProjects();

    expect(console.error).toHaveBeenCalled();
  });

  it('should handle user info loading error', () => {
    authServiceSpy.getCurrentUser.and.returnValue(null);  // forcer null ici pour déclencher getUserInfo()
    const error = new Error('Test error');
    authServiceSpy.getUserInfo.and.returnValue(throwError(() => error));
    spyOn(console, 'error');

    component.loadCurrentUser();

    expect(console.error).toHaveBeenCalled();
  });

  it('should open invite modal', () => {
    component.openInviteModal(1);
    expect(component.showInviteModal).toBeTrue();
    expect(component.selectedProjectId).toBe(1);
  });

  it('should close invite modal', () => {
    component.closeInviteModal();
    expect(component.showInviteModal).toBeFalse();
  });

  it('should handle invite submission', () => {
    spyOn(console, 'log');
    const inviteData = { email: 'test@example.com', role: ProjectRole.MEMBER };

    component.selectedProjectId = 1;
    component.handleInvite(inviteData);

    expect(console.log).toHaveBeenCalledWith(
      'Inviter:',
      inviteData.email,
      'avec le rôle:',
      inviteData.role,
      'au projet:',
      1
    );
    expect(component.showInviteModal).toBeFalse();
  });
});
