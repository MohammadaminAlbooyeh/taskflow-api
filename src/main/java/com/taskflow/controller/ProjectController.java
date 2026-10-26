package com.taskflow.controller;

import com.taskflow.model.dto.ProjectDTO;
import com.taskflow.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> create(@Valid @RequestBody ProjectDTO dto,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.create(dto, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<Page<ProjectDTO>> getMine(@AuthenticationPrincipal UserDetails userDetails,
                                                      Pageable pageable) {
        return ResponseEntity.ok(projectService.getForOwner(userDetails.getUsername(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> update(@PathVariable Long id,
                                              @Valid @RequestBody ProjectDTO dto,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(projectService.update(id, dto, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        projectService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
