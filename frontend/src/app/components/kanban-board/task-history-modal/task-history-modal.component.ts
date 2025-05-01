import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TaskHistory, TaskHistoryService } from '../../../services/task-history.service';

@Component({
  selector: 'app-task-history-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './task-history-modal.component.html',
  styleUrls: ['./task-history-modal.component.css']
})
export class TaskHistoryModalComponent {
  @Input() taskId: number | null = null;
  @Output() close = new EventEmitter<void>();

  taskHistory: TaskHistory[] = [];
  isLoading: boolean = false;

  constructor(private taskHistoryService: TaskHistoryService) {}

  ngOnInit(): void {
    if (this.taskId) {
      this.loadTaskHistory();
    }
  }

  loadTaskHistory(): void {
    this.isLoading = true;
    this.taskHistoryService.getTaskHistory(this.taskId!).subscribe({
      next: (history) => {
        this.taskHistory = history;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement de l\'historique:', error);
        this.isLoading = false;
      }
    });
  }

  onClose(): void {
    this.close.emit();
  }
} 