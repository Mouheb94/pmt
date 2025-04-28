export enum ProjectRole {
  ADMIN = 'ADMIN',
  MEMBER = 'MEMBER',
  OBSERVATOR = 'OBSERVATOR'
}

export interface ProjectMember {
  userId: number;
  projectId: number;
  role: ProjectRole;
} 