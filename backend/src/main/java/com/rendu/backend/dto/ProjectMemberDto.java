package com.rendu.backend.dto;

import com.rendu.backend.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMemberDto {
    private Long userId;
    private String username;
    private RoleName role;
}
