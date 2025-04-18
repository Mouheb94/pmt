package com.rendu.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    // The creator/admin of the project
    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    // Project members with roles
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectMember> members = new HashSet<>();

    // Tasks within the project
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();

    public Project() {
    }

    public Project(String name, String description, LocalDate startDate, User createdBy) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.createdBy = createdBy;
    }



    public Set<ProjectMember> getMembers() {
        return members;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

}

