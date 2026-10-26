package com.taskflow.mapper;

import com.taskflow.model.dto.ProjectDTO;
import com.taskflow.model.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectDTO toDTO(Project project) {
        if (project == null) {
            return null;
        }
        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .ownerId(project.getOwner() != null ? project.getOwner().getId() : null)
                .createdAt(project.getCreatedAt())
                .build();
    }
}
