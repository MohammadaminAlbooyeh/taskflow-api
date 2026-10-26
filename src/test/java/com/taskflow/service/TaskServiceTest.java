package com.taskflow.service;

import com.taskflow.exception.UnauthorizedException;
import com.taskflow.exception.ValidationException;
import com.taskflow.mapper.TaskMapper;
import com.taskflow.model.dto.TaskDTO;
import com.taskflow.model.entity.Project;
import com.taskflow.model.entity.Task;
import com.taskflow.model.entity.User;
import com.taskflow.repository.TaskRepository;
import com.taskflow.validation.TaskValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    private TaskService taskService;

    private User owner;
    private Project project;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, projectService, userService, new TaskMapper(), new TaskValidator());
        owner = User.builder().id(1L).email("owner@taskflow.dev").fullName("Owner").build();
        project = Project.builder().id(10L).name("Project A").owner(owner).build();
    }

    @Test
    void create_withBlankTitle_throwsValidationException() {
        TaskDTO dto = TaskDTO.builder().title("  ").projectId(10L).build();

        assertThatThrownBy(() -> taskService.create(dto, "owner@taskflow.dev"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void create_withPastDueDate_throwsValidationException() {
        TaskDTO dto = TaskDTO.builder()
                .title("Task")
                .projectId(10L)
                .dueDate(LocalDate.now().minusDays(1))
                .build();

        assertThatThrownBy(() -> taskService.create(dto, "owner@taskflow.dev"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void create_byNonProjectOwner_throwsUnauthorized() {
        when(projectService.findEntity(10L)).thenReturn(project);

        TaskDTO dto = TaskDTO.builder().title("Task").projectId(10L).build();

        assertThatThrownBy(() -> taskService.create(dto, "intruder@taskflow.dev"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void create_byProjectOwner_savesTask() {
        when(projectService.findEntity(10L)).thenReturn(project);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskDTO dto = TaskDTO.builder().title("Task").projectId(10L).build();

        TaskDTO result = taskService.create(dto, "owner@taskflow.dev");

        assertThat(result.getTitle()).isEqualTo("Task");
        assertThat(result.getProjectId()).isEqualTo(10L);
    }

    @Test
    void delete_whenTaskMissing_throwsResourceNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete(99L, "owner@taskflow.dev"))
                .isInstanceOf(com.taskflow.exception.ResourceNotFoundException.class);
    }
}
