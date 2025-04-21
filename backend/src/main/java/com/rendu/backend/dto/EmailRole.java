package com.rendu.backend.dto;

import com.rendu.backend.enums.RoleName;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class EmailRole {
        private String email;
        private RoleName role;

}
