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
  @Output() close = new EventEmitter<void>();
  @Output() taskUpdated = new EventEmitter<Task>();

  taskForm: FormGroup;
  isEditing: boolean = false;

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService
  ) {
    this.taskForm = this.fb.group({
      name: [{ value: '', disabled: true }, [Validators.required, Validators.minLength(3)]],
      description: [{ value: '', disabled: true }],
      dueDate: [{ value: '', disabled: true }, Validators.required],
      priority: [{ value: 'MEDIUM', disabled: true }, Validators.required],
      status: [{ value: 'TODO', disabled: true }, Validators.required],
      assignedTo: [{ value: null, disabled: true }]
    });
  }

  ngOnChanges(): void {
    if (this.task) {
      this.taskForm.patchValue({
        name: this.task.name,
        description: this.task.description,
        dueDate: this.task.dueDate,
        priority: this.task.priority,
        status: this.task.status,
        assignedTo: this.task.assignedTo?.id || null
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
      const assignedToId = formValue.assignedTo;
      const currentTask = this.task;

      // Si l'assignation a changé, utiliser la méthode assignTask
      if (assignedToId !== (currentTask.assignedTo?.id || null)) {
        this.taskService.assignTask(currentTask.id, assignedToId).subscribe({
          next: (task) => {
            console.log('Tâche assignée avec succès:', task);
            // Mettre à jour les autres champs si nécessaire
            if (formValue.name !== currentTask.name || 
                formValue.description !== currentTask.description ||
                formValue.dueDate !== currentTask.dueDate ||
                formValue.priority !== currentTask.priority ||
                formValue.status !== currentTask.status) {
              
              const updatedTask = {
                ...currentTask,
                ...formValue,
                assignedTo: assignedToId ? 
                  this.projectMembers.find(m => m.userId === assignedToId) : null
              };

              this.taskService.updateTask(currentTask.id, updatedTask).subscribe({
                next: (finalTask) => {
                  this.taskUpdated.emit(finalTask);
                  this.isEditing = false;
                  this.taskForm.disable();
                },
                error: (error) => {
                  console.error('Erreur lors de la mise à jour de la tâche:', error);
                }
              });
            } else {
              this.taskUpdated.emit(task);
              this.isEditing = false;
              this.taskForm.disable();
            }
          },
          error: (error) => {
            console.error('Erreur lors de l\'assignation de la tâche:', error);
          }
        });
      } else {
        // Si l'assignation n'a pas changé, mettre à jour la tâche normalement
        const updatedTask = {
          ...currentTask,
          ...formValue,
          assignedTo: assignedToId ? 
            this.projectMembers.find(m => m.userId === assignedToId) : null
        };

        this.taskService.updateTask(currentTask.id, updatedTask).subscribe({
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
        status: this.task.status,
        assignedTo: this.task.assignedTo?.id || null
      });
    }
  }

  onClose(): void {
    this.close.emit();
  }
} 