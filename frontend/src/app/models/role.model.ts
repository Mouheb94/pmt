export enum ProjectRole {
  ADMIN = 'ADMIN',
  MEMBER = 'MEMBER',
  VIEWER = 'VIEWER'
}

export interface ProjectMember {
  userId: number;
  projectId: number;
  role: ProjectRole;
} 