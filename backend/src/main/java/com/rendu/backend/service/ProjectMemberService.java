package com.rendu.backend.service;


import com.rendu.backend.models.ProjectMember;

import java.util.List;

public interface ProjectMemberService {
    ProjectMember addMemberToProject(ProjectMember projectMember);
    List<ProjectMember> getMembersByProject(Long projectId);
    List<ProjectMember> getProjectsByUser(Long userId);
    void removeMember(Long memberId);
}

