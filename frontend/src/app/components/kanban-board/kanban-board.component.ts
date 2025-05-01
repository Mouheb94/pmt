import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Task } from '../../models/project.model';
import { ProjectRole } from '../../models/role.model';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { TaskDetailsModalComponent } from '../project-details/task-details-modal/task-details-modal.component';
import { TaskService } from '../../services/task.service';
import { TaskHistoryModalComponent } from './task-history-modal/task-history-modal.component';

@Component({
  selector: 'app-kanban-board',
  standalone: true,
  imports: [CommonModule, TaskDetailsModalComponent, TaskHistoryModalComponent],
  templateUrl: './kanban-board.component.html',
  styleUrls: ['./kanban-board.component.css']
})
export class KanbanBoardComponent implements OnInit {
  @Input() tasks: Task[] = [];
  @Input() projectId: number = 0;
  @Input() projectMembers: { userId: number; username: string; role: ProjectRole; }[] = [];
  
  todoTasks: Task[] = [];
  inProgressTasks: Task[] = [];
  doneTasks: Task[] = [];
  currentUser: User | null = null;
  selectedTask: Task | null = null;
  currentUserRole: ProjectRole | null = null;
  showAssignModal: boolean = false;
  taskToAssign: Task | null = null;
  showTaskHistoryModal: boolean = false;
  selectedTaskForHistory: Task | null = null;

  constructor(
    private authService: AuthService,
    private taskService: TaskService
  ) {
    this.currentUser = this.authService.getCurrentUser();
  }

  ngOnInit(): void {
    this.updateTaskLists();
    this.setCurrentUserRole();
  }

  ngOnChanges(): void {
    if (this.tasks && this.tasks.length !== 0) {
      this.updateTaskLists();
    }
    this.setCurrentUserRole();
  }

  private updateTaskLists(): void {
    this.todoTasks = this.tasks.filter(task => task.status === 'TODO');
    this.inProgressTasks = this.tasks.filter(task => task.status === 'IN_PROGRESS');
    this.doneTasks = this.tasks.filter(task => task.status === 'DONE');
  }

  private setCurrentUserRole(): void {
    if (this.currentUser && this.projectMembers) {
      const member = this.projectMembers.find(m => m.userId === this.currentUser?.id);
      this.currentUserRole = member?.role || null;
    }
  }

  openTaskDetails(task: Task): void {
    this.selectedTask = task;
  }

  closeTaskDetails(): void {
    this.selectedTask = null;
  }

  handleTaskUpdated(updatedTask: Task): void {
    const index = this.tasks.findIndex(t => t.id === updatedTask.id);
    if (index !== -1) {
      const existingTask = this.tasks[index];
      this.tasks[index] = {
        ...updatedTask,
        assignedToId: existingTask.assignedToId,
        assignedToUsername: existingTask.assignedToUsername
      };
      this.updateTaskLists();
    }
    this.closeTaskDetails();
  }

  canCreateTask(): boolean {
    console.log(this.currentUser?.id+"currentUser");
    if (!this.currentUser) return false;
    const member = this.projectMembers.find(m => m.userId === this.currentUser?.id);
    console.log(this.projectMembers+"projectMembers");
    console.log(member+"member");
    return member?.role === ProjectRole.ADMIN || member?.role === ProjectRole.MEMBER;
  }

  createTask(): void {
    // TODO: Implémenter la logique de création de tâche
    console.log('Création d\'une nouvelle tâche');
  }

  canAssignTask(): boolean {
    return this.currentUserRole === ProjectRole.ADMIN || this.currentUserRole === ProjectRole.MEMBER;
  }

  openAssignModal(task: Task, event: Event): void {
    event.stopPropagation();
    this.taskToAssign = task;
    this.showAssignModal = true;
  }

  closeAssignModal(): void {
    this.showAssignModal = false;
    this.taskToAssign = null;
  }

  assignTask(userId: number): void {
    if (this.taskToAssign) {
      this.taskService.assignTask(this.taskToAssign.id, userId).subscribe({
        next: (task) => {
          
          const index = this.tasks.findIndex(t => t.id === task.id);
          if (index !== -1) {
            this.tasks[index] = task;
            this.updateTaskLists();
          }
          this.closeAssignModal();
        },
        error: (error) => {
          console.error('Erreur lors de l\'assignation de la tâche:', error);
        }
      });
    }
  }

  openTaskHistory(task: Task, event: Event): void {
    event.stopPropagation();
    this.selectedTaskForHistory = task;
    this.showTaskHistoryModal = true;
  }

  closeTaskHistory(): void {
    this.showTaskHistoryModal = false;
    this.selectedTaskForHistory = null;
  }
} 