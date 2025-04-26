package com.rendu.backend.dao;


import com.rendu.backend.dto.ProjectDto;
import com.rendu.backend.models.Project;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}

