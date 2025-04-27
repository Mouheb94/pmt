import { ProjectRole } from './role.model';

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
    id: number;
    user: {
      id: number;
      username: string;
    };
    role: ProjectRole;
  }[];
  tasks: {
    id: number;
    name: string;
    description: string;
    dueDate: string;
    priority: string;
    status: string;
    assignedTo: {
      id: number;
      username: string;
    } | null;
  }[];
}

export interface ProjectDto {
  id?: number;
  name: string;
  description: string;
  startDate: Date;
} 