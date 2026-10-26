package com.taskflow.model.dto;

import com.taskflow.model.enums.TaskPriority;
import com.taskflow.model.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 4000)
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;

    @NotNull
    private Long projectId;

    private Long assigneeId;
    private Instant createdAt;
    private Instant updatedAt;
}
