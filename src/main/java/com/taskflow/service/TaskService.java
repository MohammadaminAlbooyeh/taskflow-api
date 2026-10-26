package com.taskflow.service;

import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.exception.UnauthorizedException;
import com.taskflow.mapper.TaskMapper;
import com.taskflow.model.dto.TaskDTO;
import com.taskflow.model.entity.Project;
import com.taskflow.model.entity.Task;
import com.taskflow.model.entity.User;
import com.taskflow.repository.TaskRepository;
import com.taskflow.validation.TaskValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final TaskMapper taskMapper;
    private final TaskValidator taskValidator;

    public TaskService(TaskRepository taskRepository,
                        ProjectService projectService,
                        UserService userService,
                        TaskMapper taskMapper,
                        TaskValidator taskValidator) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.taskMapper = taskMapper;
        this.taskValidator = taskValidator;
    }

    public TaskDTO create(TaskDTO dto, String requesterEmail) {
        taskValidator.validate(dto);

        Project project = projectService.findEntity(dto.getProjectId());
        requireProjectAccess(project, requesterEmail);

        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : com.taskflow.model.enums.TaskStatus.TODO)
                .priority(dto.getPriority() != null ? dto.getPriority() : com.taskflow.model.enums.TaskPriority.MEDIUM)
                .dueDate(dto.getDueDate())
                .project(project)
                .assignee(dto.getAssigneeId() != null ? userService.getEntityById(dto.getAssigneeId()) : null)
                .build();

        return taskMapper.toDTO(taskRepository.save(task));
    }

    public TaskDTO getById(Long id) {
        return taskMapper.toDTO(findEntity(id));
    }

    public List<TaskDTO> getForProject(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    public TaskDTO update(Long id, TaskDTO dto, String requesterEmail) {
        taskValidator.validate(dto);

        Task task = findEntity(id);
        requireProjectAccess(task.getProject(), requesterEmail);

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        task.setAssignee(dto.getAssigneeId() != null ? userService.getEntityById(dto.getAssigneeId()) : null);

        return taskMapper.toDTO(taskRepository.save(task));
    }

    public void delete(Long id, String requesterEmail) {
        Task task = findEntity(id);
        requireProjectAccess(task.getProject(), requesterEmail);
        taskRepository.delete(task);
    }

    private Task findEntity(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }

    private void requireProjectAccess(Project project, String requesterEmail) {
        User owner = project.getOwner();
        if (!owner.getEmail().equals(requesterEmail)) {
            throw new UnauthorizedException("You do not have access to this project's tasks");
        }
    }
}
