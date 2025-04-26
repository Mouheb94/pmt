import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProjectService, Project, ProjectDto } from '../services/project.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  projects: Project[] = [];
  activeTab: string = 'creation';
  projectForm: FormGroup;

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
} 