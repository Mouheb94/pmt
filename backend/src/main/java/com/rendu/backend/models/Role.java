package com.rendu.backend.models;

import com.rendu.backend.enums.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false)
    private RoleName name;
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role(RoleName name) {
        this.name = name;
    }
    public Role() {
    }


    public Role(long l, String admin) {
    }
}
