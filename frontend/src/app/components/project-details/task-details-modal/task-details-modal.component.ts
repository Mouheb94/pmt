import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Task } from '../../../models/project.model';
import { ProjectRole } from '../../../models/role.model';
import { TaskService } from '../../../services/task.service';

@Component({
  selector: 'app-task-details-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './task-details-modal.component.html',
  styleUrls: ['./task-details-modal.component.css']
})
export class TaskDetailsModalComponent {
  @Input() task: Task | null = null;
  @Input() projectMembers: { userId: number; username: string; role: ProjectRole; }[] = [];
  @Input() currentUserRole: ProjectRole | null = null;
  @Input() currentUserId: number | null = null;
  @Output() close = new EventEmitter<void>();
  @Output() taskUpdated = new EventEmitter<Task>();

  taskForm: FormGroup;
  isEditing: boolean = false;
  showAssignModal: boolean = false;

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService
  ) {
    this.taskForm = this.fb.group({
      name: [{ value: '', disabled: true }, [Validators.required, Validators.minLength(3)]],
      description: [{ value: '', disabled: true }],
      dueDate: [{ value: '', disabled: true }, Validators.required],
      priority: [{ value: 'MEDIUM', disabled: true }, Validators.required],
      status: [{ value: 'TODO', disabled: true }, Validators.required]
    });
  }

  ngOnChanges(): void {
    if (this.task) {
      this.taskForm.patchValue({
        name: this.task.name,
        description: this.task.description,
        dueDate: this.task.dueDate,
        priority: this.task.priority,
        status: this.task.status
      });
    }
  }

  canEdit(): boolean {
    return this.currentUserRole === ProjectRole.ADMIN || this.currentUserRole === ProjectRole.MEMBER;
  }

  startEditing(): void {
    this.isEditing = true;
    this.taskForm.enable();
  }

  saveChanges(): void {
    if (this.taskForm.valid && this.task) {
      const formValue = this.taskForm.value;
      const currentTask = this.task;

      const updatedTask = {
        ...currentTask,
        ...formValue
      };

      this.taskService.updateTask(currentTask.id, updatedTask,this.currentUserId).subscribe({
        next: (task) => {
          this.taskUpdated.emit(task);
          this.isEditing = false;
          this.taskForm.disable();
        },
        error: (error) => {
          console.error('Erreur lors de la mise à jour de la tâche:', error);
        }
      });
    }
  }

  cancelEditing(): void {
    this.isEditing = false;
    this.taskForm.disable();
    if (this.task) {
      this.taskForm.patchValue({
        name: this.task.name,
        description: this.task.description,
        dueDate: this.task.dueDate,
        priority: this.task.priority,
        status: this.task.status
      });
    }
  }

  onClose(): void {
    this.close.emit();
  }

} 