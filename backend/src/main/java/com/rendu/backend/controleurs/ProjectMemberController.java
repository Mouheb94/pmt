package com.rendu.backend.controleurs;

import com.rendu.backend.models.ProjectMember;
import com.rendu.backend.service.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pmt/project-members")
@CrossOrigin("*")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @Autowired
    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @PostMapping
    public ProjectMember addMember(@RequestBody ProjectMember member) {
        return projectMemberService.addMemberToProject(member);
    }

    @GetMapping("/{projectId}")
    public List<ProjectMember> getMembersByProject(@PathVariable Long projectId) {
        return projectMemberService.getMembersByProject(projectId);
    }

    @GetMapping("/user/{userId}")
    public List<ProjectMember> getProjectsByUser(@PathVariable Long userId) {
        return projectMemberService.getProjectsByUser(userId);
    }


}

