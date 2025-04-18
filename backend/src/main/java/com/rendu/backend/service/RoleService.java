package com.rendu.backend.service;

import com.rendu.backend.enums.RoleName;
import com.rendu.backend.models.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(RoleName name);
    List<Role> getAllRoles();
}
