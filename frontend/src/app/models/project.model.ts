import { ProjectRole } from './role.model';

export interface Task {
  id: number;
  name: string;
  description: string;
  dueDate: string;
  priority: string;
  status: string;
  createdBy: {
    id: number;
    username: string;
  };
  assignedTo?: {
    id: number;
    username: string;
  };
  assignedToId?: number;
  assignedToUsername?: string;
}

export interface Project {
  id: number;
  name: string;
  description: string;
  startDate: string;
  createdBy: {
    id: number;
    username: string;
  };
  members: {
      userId: number;
      username: string;
      role: ProjectRole;
  }[];
  tasks: Task[];
}

export interface ProjectDto {
  id?: number;
  name: string;
  description: string;
  startDate: Date;
} 