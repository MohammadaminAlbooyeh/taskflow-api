package com.taskflow.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 2000)
    private String description;
    private Long ownerId;
    private Instant createdAt;
}
