import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from '../../services/project.service';
import { TaskService } from '../../services/task.service';
import { Project } from '../../models/project.model';
import { ProjectRole } from '../../models/role.model';
import { MembersModalComponent } from './members-modal/members-modal.component';
import { KanbanBoardComponent } from '../kanban-board/kanban-board.component';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { CreateTaskModalComponent } from './create-task-modal/create-task-modal.component';

@Component({
  selector: 'app-project-details',
  standalone: true,
  imports: [CommonModule, MembersModalComponent, KanbanBoardComponent, CreateTaskModalComponent],
  templateUrl: './project-details.component.html',
  styleUrls: ['./project-details.component.css']
})
export class ProjectDetailsComponent implements OnInit {
  project: Project | null = null;
  showMembersModal: boolean = false;
  showCreateTaskModal: boolean = false;
  currentUser: User | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService,
    private taskService: TaskService,
    private authService: AuthService
  ) {
    this.currentUser = this.authService.getCurrentUser();
  }

  ngOnInit(): void {
    const projectId = this.route.snapshot.paramMap.get('id');
    if (projectId) {
      this.loadProjectDetails(parseInt(projectId));
    }
  }

  loadProjectDetails(id: number): void {
    this.projectService.getProjectDetails(id).subscribe({
      next: (project) => {
        this.project = project;
        console.log(this.project.tasks[0].name +"projectMouheb");
      },
      error: (error) => {
        console.error('Erreur lors du chargement des détails du projet:', error);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  openMembersModal(): void {
    this.showMembersModal = true;
  }

  closeMembersModal(): void {
    this.showMembersModal = false;
  }

  canCreateTask(): boolean {
    if (!this.currentUser || !this.project) return false;
    const member = this.project.members.find(m => m.userId === this.currentUser?.id);
    return member?.role === ProjectRole.ADMIN || member?.role === ProjectRole.MEMBER;
  }

  createTask(): void {
    this.showCreateTaskModal = true;
  }

  handleTaskCreated(taskData: any): void {
    if (this.project) {
      this.taskService.createTask(this.project.id, {
        ...taskData,
        createdBy: {
            id: this.currentUser?.id,
            username: this.currentUser?.username
          },
        status: 'TODO' // Nouvelle tâche est toujours en TODO
      }).subscribe({
        next: () => {
          this.showCreateTaskModal = false;
          // Recharger uniquement les tâches du projet
          this.taskService.getTasksByProject(this.project!.id).subscribe({
            next: (tasks) => {
               
              if (this.project) {
                this.project.tasks = tasks;
                console.log(tasks+"tasksaLL");
              }
            },
            error: (error) => {
              console.error('Erreur lors du chargement des tâches:', error);
            }
          });
        },
        error: (error) => {
          console.error('Erreur lors de la création de la tâche:', error);
          // TODO: Afficher un message d'erreur à l'utilisateur
        }
      });
    }
  }

  closeCreateTaskModal(): void {
    this.showCreateTaskModal = false;
  }
} 