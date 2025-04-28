import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProjectService } from '../../services/project.service';
import { AuthService } from '../../services/auth.service';
import { Project, ProjectDto } from '../../models/project.model';
import { User } from '../../models/user.model';
import { ProjectRole } from '../../models/role.model';
import { InviteModalComponent } from './invite-modal/invite-modal.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule, InviteModalComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  projects: Project[] = [];
  activeTab: string = 'dashboard';
  projectForm: FormGroup;
  currentUser: User | null = null;
  showInviteModal: boolean = false;
  selectedProjectId: number = 0;

  constructor(
    private router: Router,
    private projectService: ProjectService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.projectForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      startDate: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadProjects();
    this.loadCurrentUser();
  }

  loadCurrentUser(): void {
    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) {
      this.authService.getUserInfo().subscribe({
        next: (user) => {
          this.currentUser = user;
        },
        error: (error) => {
          console.error('Erreur lors du chargement des informations utilisateur:', error);
        }
      });
    }
  }

  isProjectAdmin(project: Project): boolean {
    if (!this.currentUser) return false;
    const member = project.members.find(m => m.userId === this.currentUser?.id);
    console.log(member+"memberCONNECTER");
    return member?.role === ProjectRole.ADMIN;
  }

  loadProjects(): void {
    this.projectService.getProjects().subscribe({
      next: (projects) => {
        this.projects = projects;
        console.log("projects");
        console.log(this.projects);
      },
      error: (error) => {
        console.error('Erreur lors du chargement des projets:', error);
      }
    });
  }

  deleteProject(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce projet ?')) {
      this.projectService.deleteProject(id).subscribe({
        next: () => {
          this.projects = this.projects.filter(project => project.id !== id);
        },
        error: (error) => {
          console.error('Erreur lors de la suppression du projet:', error);
        }
      });
    }
  }

  getActiveTabTitle(): string {
    switch(this.activeTab) {
      case 'creation':
        return 'Création de projet';
      case 'dashboard':
        return 'Tableau de bord';
      default:
        return '';
    }
  }

  onSubmit(): void {
    if (this.projectForm.valid) {
      const formValue = this.projectForm.value;
      const projectDto: ProjectDto = {
        name: formValue.name,
        description: formValue.description,
        startDate: new Date(formValue.startDate + 'T00:00:00'),
       // createdById: 1 // TODO: Récupérer l'ID de l'utilisateur connecté
      };
      
      this.projectService.createProject(projectDto).subscribe({
        next: (response) => {
          console.log('Projet créé avec succès:', response);
          this.resetForm();
          this.setActiveTab('dashboard');
        },
        error: (error) => {
          console.error('Erreur lors de la création du projet:', error);
        }
      });
    }
  }

  resetForm(): void {
    this.projectForm.reset();
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  openInviteModal(projectId: number): void {
    this.selectedProjectId = projectId;
    this.showInviteModal = true;
  }

  closeInviteModal(): void {
    this.showInviteModal = false;
  }

  handleInvite(data: {email: string, role: ProjectRole}): void {
    console.log('Inviter:', data.email, 'avec le rôle:', data.role, 'au projet:', this.selectedProjectId);
    this.closeInviteModal();
  }
} 