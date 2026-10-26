package com.taskflow.service;

import com.taskflow.exception.UnauthorizedException;
import com.taskflow.mapper.ProjectMapper;
import com.taskflow.model.dto.ProjectDTO;
import com.taskflow.model.entity.Project;
import com.taskflow.model.entity.Role;
import com.taskflow.model.entity.User;
import com.taskflow.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    private ProjectService projectService;

    private User owner;
    private Project project;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository, userService, new ProjectMapper());
        owner = User.builder().id(1L).email("owner@taskflow.dev").fullName("Owner").build();
        project = Project.builder().id(10L).name("Project A").owner(owner).build();
    }

    @Test
    void create_buildsProjectOwnedByCurrentUser() {
        when(userService.getEntityByEmail("owner@taskflow.dev")).thenReturn(owner);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectDTO dto = ProjectDTO.builder().name("New Project").description("desc").build();

        ProjectDTO result = projectService.create(dto, "owner@taskflow.dev");

        assertThat(result.getName()).isEqualTo("New Project");
        assertThat(result.getOwnerId()).isEqualTo(1L);
    }

    @Test
    void update_byNonOwner_throwsUnauthorized() {
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(userService.getEntityByEmail("someone-else@taskflow.dev"))
                .thenReturn(User.builder().id(2L).email("someone-else@taskflow.dev").role(Role.USER).build());

        ProjectDTO dto = ProjectDTO.builder().name("Renamed").build();

        assertThatThrownBy(() -> projectService.update(10L, dto, "someone-else@taskflow.dev"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void update_byAdmin_bypassesOwnershipCheck() {
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userService.getEntityByEmail("admin@taskflow.dev"))
                .thenReturn(User.builder().id(3L).email("admin@taskflow.dev").role(Role.ADMIN).build());

        ProjectDTO dto = ProjectDTO.builder().name("Renamed by admin").build();

        ProjectDTO result = projectService.update(10L, dto, "admin@taskflow.dev");

        assertThat(result.getName()).isEqualTo("Renamed by admin");
    }

    @Test
    void update_byOwner_updatesFields() {
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectDTO dto = ProjectDTO.builder().name("Renamed").description("new desc").build();

        ProjectDTO result = projectService.update(10L, dto, "owner@taskflow.dev");

        assertThat(result.getName()).isEqualTo("Renamed");
        assertThat(result.getDescription()).isEqualTo("new desc");
    }
}
