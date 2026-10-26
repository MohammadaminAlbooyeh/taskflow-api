package com.taskflow.repository;

import com.taskflow.model.entity.Task;
import com.taskflow.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    Page<Task> findByProjectId(Long projectId, Pageable pageable);
    List<Task> findByAssigneeId(Long assigneeId);
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);
}
