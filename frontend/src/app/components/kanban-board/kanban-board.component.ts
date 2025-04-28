import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Task } from '../../models/project.model';
import { ProjectRole } from '../../models/role.model';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-kanban-board',
  standalone: true,
  imports: [CommonModule],
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

  constructor(private authService: AuthService) {
    this.currentUser = this.authService.getCurrentUser();
  }

  ngOnInit(): void {
    if (this.tasks && this.tasks.length > 0) {
        this.updateTaskLists();
      } else {
        console.log("No tasks available");
      }
     }

  ngOnChanges(): void {
    if (this.tasks && this.tasks.length > 0) {
      this.updateTaskLists();
    } else {
      console.log("No tasks available");
    }
  }

  private updateTaskLists(): void {
    this.todoTasks = this.tasks.filter(task => task.status === 'TODO');
    console.log(this.tasks[0].name+"todoTasks");
    this.inProgressTasks = this.tasks.filter(task => task.status === 'IN_PROGRESS');
    this.doneTasks = this.tasks.filter(task => task.status === 'DONE');
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