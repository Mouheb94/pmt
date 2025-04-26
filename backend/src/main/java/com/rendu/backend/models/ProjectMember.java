package com.rendu.backend.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rendu.backend.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Table(name = "project_member",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "project_id"}))
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;

    // The role of this user in the project (ADMIN, MEMBER, OBSERVER)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName role;


    public ProjectMember(Long id, String username, RoleName role) {

    }

    public ProjectMember(User user, Project project, RoleName roleName) {
    }
}





