import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProjectRole } from '../../models/role.model';
import { ProjectService } from '../../services/project.service';
import { EmailRole } from '../../models/email-role.model';

@Component({
  selector: 'app-invite-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './invite-modal.component.html',
  styleUrls: ['./invite-modal.component.css']
})
export class InviteModalComponent {
  @Input() projectId: number = 0;
  @Output() invite = new EventEmitter<EmailRole>();
  @Output() closeModal = new EventEmitter<void>();

  inviteForm: FormGroup;
  roles = Object.values(ProjectRole);

  constructor(private fb: FormBuilder, private projectService: ProjectService) {
    this.inviteForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      role: [ProjectRole.MEMBER, Validators.required]
    });
  }

  onSubmit(): void {
    if (this.inviteForm.valid) {
      const emailRole: EmailRole = this.inviteForm.value;
      this.projectService.inviteMembers(this.projectId, [emailRole]).subscribe({
        next: () => {
          this.invite.emit(emailRole);
          this.inviteForm.reset({ role: ProjectRole.MEMBER });
        },
        error: (error) => {
          console.error('Erreur lors de l\'invitation:', error);
        }
      });
    }
  }

  close(): void {
    this.closeModal.emit();
  }
} 