package com.taskflow.service;

import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.exception.UnauthorizedException;
import com.taskflow.mapper.ProjectMapper;
import com.taskflow.model.dto.ProjectDTO;
import com.taskflow.model.entity.Project;
import com.taskflow.model.entity.User;
import com.taskflow.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectMapper projectMapper;

    public ProjectService(ProjectRepository projectRepository, UserService userService, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.projectMapper = projectMapper;
    }

    public ProjectDTO create(ProjectDTO dto, String ownerEmail) {
        User owner = userService.getEntityByEmail(ownerEmail);

        Project project = Project.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .owner(owner)
                .build();

        return projectMapper.toDTO(projectRepository.save(project));
    }

    public ProjectDTO getById(Long id) {
        return projectMapper.toDTO(findEntity(id));
    }

    public List<ProjectDTO> getForOwner(String ownerEmail) {
        User owner = userService.getEntityByEmail(ownerEmail);
        return projectRepository.findByOwnerId(owner.getId()).stream()
                .map(projectMapper::toDTO)
                .toList();
    }

    public ProjectDTO update(Long id, ProjectDTO dto, String requesterEmail) {
        Project project = findEntity(id);
        requireOwner(project, requesterEmail);

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());

        return projectMapper.toDTO(projectRepository.save(project));
    }

    public void delete(Long id, String requesterEmail) {
        Project project = findEntity(id);
        requireOwner(project, requesterEmail);
        projectRepository.delete(project);
    }

    Project findEntity(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    private void requireOwner(Project project, String requesterEmail) {
        if (!project.getOwner().getEmail().equals(requesterEmail)) {
            throw new UnauthorizedException("You do not have access to this project");
        }
    }
}
