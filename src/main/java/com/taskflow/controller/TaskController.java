package com.taskflow.controller;

import com.taskflow.model.dto.TaskDTO;
import com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskDTO> create(@Valid @RequestBody TaskDTO dto,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(dto, userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getForProject(@RequestParam Long projectId) {
        return ResponseEntity.ok(taskService.getForProject(projectId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> update(@PathVariable Long id,
                                           @Valid @RequestBody TaskDTO dto,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.update(id, dto, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        taskService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
