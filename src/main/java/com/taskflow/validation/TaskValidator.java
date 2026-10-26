package com.taskflow.validation;

import com.taskflow.exception.ValidationException;
import com.taskflow.model.dto.TaskDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TaskValidator {

    public void validate(TaskDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new ValidationException("Task title must not be blank");
        }
        if (dto.getProjectId() == null) {
            throw new ValidationException("Task must belong to a project");
        }
        if (dto.getDueDate() != null && dto.getDueDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Due date cannot be in the past");
        }
    }
}
