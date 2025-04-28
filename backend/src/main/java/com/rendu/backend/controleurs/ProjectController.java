package com.rendu.backend.controleurs;


import com.rendu.backend.dto.EmailRole;
import com.rendu.backend.dto.ProjectCreateDto;
import com.rendu.backend.dto.ProjectDto;
import com.rendu.backend.models.Project;
import com.rendu.backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pmt/projects")
@CrossOrigin("*")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

 /*   @PostMapping("/create")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
        ProjectDto createProject = projectService.createProject(projectDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createProject);
    }*/

    @PostMapping("/create")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectCreateDto projectDto) {
        ProjectDto createProject = projectService.createProject(projectDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createProject);
    }

    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }
    @GetMapping("/details")
    public List<ProjectDto> getAllProjectsDto() {
        return projectService.getAllProjectsDto();
    }

    @GetMapping("/{id}")
    public ProjectDto getProjectById(@PathVariable Long id) {

        return projectService.getProjectById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id,@RequestBody ProjectDto projectDto) {
        return ResponseEntity.ok(projectService.updateProject(id, projectDto));
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {

        projectService.deleteProject(id);
    }

    @PostMapping("/invite/{projectId}")
    public void inviteMembres(@PathVariable Long projectId, @RequestBody List<EmailRole> emailRoles){
        projectService.inviteMembres(projectId,emailRoles);
    }
}

