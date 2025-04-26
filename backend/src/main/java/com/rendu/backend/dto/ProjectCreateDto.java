package com.rendu.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ProjectCreateDto {
    private String name;
    private String description;
    private LocalDate startDate;

}
