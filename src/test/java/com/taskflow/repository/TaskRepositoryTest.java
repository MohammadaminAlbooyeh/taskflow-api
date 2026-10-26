package com.taskflow.repository;

import com.taskflow.model.entity.Project;
import com.taskflow.model.entity.Task;
import com.taskflow.model.entity.User;
import com.taskflow.model.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByProjectId_returnsTasksForThatProjectOnly() {
        User owner = userRepository.save(User.builder()
                .email("owner@taskflow.dev")
                .password("hashed")
                .fullName("Owner")
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("Project A")
                .owner(owner)
                .build());

        Project otherProject = projectRepository.save(Project.builder()
                .name("Project B")
                .owner(owner)
                .build());

        taskRepository.save(Task.builder().title("Task 1").project(project).build());
        taskRepository.save(Task.builder().title("Task 2").project(project).build());
        taskRepository.save(Task.builder().title("Task 3").project(otherProject).build());

        List<Task> tasks = taskRepository.findByProjectId(project.getId());

        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting(Task::getTitle).containsExactlyInAnyOrder("Task 1", "Task 2");
    }

    @Test
    void findByProjectIdAndStatus_filtersByStatus() {
        User owner = userRepository.save(User.builder()
                .email("owner2@taskflow.dev")
                .password("hashed")
                .fullName("Owner Two")
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("Project C")
                .owner(owner)
                .build());

        taskRepository.save(Task.builder().title("Todo task").project(project).status(TaskStatus.TODO).build());
        taskRepository.save(Task.builder().title("Done task").project(project).status(TaskStatus.DONE).build());

        List<Task> doneTasks = taskRepository.findByProjectIdAndStatus(project.getId(), TaskStatus.DONE);

        assertThat(doneTasks).hasSize(1);
        assertThat(doneTasks.get(0).getTitle()).isEqualTo("Done task");
    }
}
