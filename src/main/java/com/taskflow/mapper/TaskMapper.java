package com.taskflow.mapper;

import com.taskflow.model.dto.TaskDTO;
import com.taskflow.model.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskDTO toDTO(Task task) {
        if (task == null) {
            return null;
        }
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .projectId(task.getProject() != null ? task.getProject().getId() : null)
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
