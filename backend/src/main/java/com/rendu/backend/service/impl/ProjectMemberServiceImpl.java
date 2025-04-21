package com.rendu.backend.service.impl;


import com.rendu.backend.dao.ProjectMemberRepository;
import com.rendu.backend.models.ProjectMember;

import com.rendu.backend.service.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    @Autowired
    public ProjectMemberServiceImpl(ProjectMemberRepository projectMemberRepository) {
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    public ProjectMember addMemberToProject(ProjectMember projectMember) {
        return projectMemberRepository.save(projectMember);
    }

    @Override
    public List<ProjectMember> getMembersByProject(Long projectId) {
        return projectMemberRepository.findByProjectId(projectId);
    }

    @Override
    public List<ProjectMember> getProjectsByUser(Long userId) {
        return projectMemberRepository.findByUserId(userId);
    }

    @Override
    public void removeMember(Long memberId) {
        projectMemberRepository.deleteById(memberId);
    }
}

