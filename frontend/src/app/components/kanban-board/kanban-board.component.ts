import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Task } from '../../models/project.model';
import { ProjectRole } from '../../models/role.model';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { TaskDetailsModalComponent } from '../project-details/task-details-modal/task-details-modal.component';

@Component({
  selector: 'app-kanban-board',
  standalone: true,
  imports: [CommonModule, TaskDetailsModalComponent],
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

  constructor(private authService: AuthService) {
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
      this.tasks[index] = updatedTask;
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
} 