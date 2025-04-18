package com.rendu.backend.models;


import com.rendu.backend.enums.RoleName;
import jakarta.persistence.*;
import lombok.Setter;
@Setter
@Entity
@Table(name = "project_member")
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user in the project
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The associated project
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // The role of this user in the project (ADMIN, MEMBER, OBSERVER)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName role;

    public ProjectMember() {
    }

    public ProjectMember(User user, Project project, RoleName role) {
        this.user = user;
        this.project = project;
        this.role = role;
    }


    }



