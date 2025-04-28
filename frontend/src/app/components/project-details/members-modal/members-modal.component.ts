import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Project } from '../../../models/project.model';

@Component({
  selector: 'app-members-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './members-modal.component.html',
  styleUrls: ['./members-modal.component.css']
})
export class MembersModalComponent {
  @Input() project: Project | null = null;
  @Output() closeModal = new EventEmitter<void>();

  close(): void {
    this.closeModal.emit();
  }
} 