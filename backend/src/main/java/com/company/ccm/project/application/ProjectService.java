package com.company.ccm.project.application;

import com.company.ccm.organization.domain.RoleCodes;
import com.company.ccm.project.api.dto.ProjectDtos.CreateProjectRequest;
import com.company.ccm.project.api.dto.ProjectDtos.ProjectMemberRequest;
import com.company.ccm.project.api.dto.ProjectDtos.ProjectMemberResponse;
import com.company.ccm.project.api.dto.ProjectDtos.ProjectResponse;
import com.company.ccm.project.api.dto.ProjectDtos.ReplaceMembersRequest;
import com.company.ccm.project.api.dto.ProjectDtos.UpdateProjectRequest;
import com.company.ccm.project.domain.ProjectEntity;
import com.company.ccm.project.domain.ProjectMemberEntity;
import com.company.ccm.project.infrastructure.ProjectMemberRepository;
import com.company.ccm.project.infrastructure.ProjectRepository;
import com.company.ccm.shared.domain.BusinessException;
import com.company.ccm.shared.domain.NotFoundException;
import com.company.ccm.shared.security.SecurityUtils;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectAccessService projectAccessService;

    public ProjectService(
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            ProjectAccessService projectAccessService) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectAccessService = projectAccessService;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> list() {
        if (SecurityUtils.hasRole(RoleCodes.SYSTEM_ADMIN)) {
            return projectRepository.findAll().stream().map(this::toResponse).toList();
        }
        String userId = SecurityUtils.currentUserId();
        return projectMemberRepository.findByUserId(userId).stream()
                .map(m -> projectRepository.findById(m.getProjectId()).orElse(null))
                .filter(p -> p != null)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse get(String id) {
        projectAccessService.requireProjectAccess(id);
        return toResponse(require(id));
    }

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.PROJECT_ADMIN);
        if (projectRepository.existsByCode(request.code())) {
            throw new BusinessException("Project code already exists");
        }
        ProjectEntity project = new ProjectEntity();
        project.setCode(request.code());
        project.setName(request.name());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setManagerUserId(request.managerUserId());
        project.setStatus("ACTIVE");
        ProjectEntity saved = projectRepository.save(project);

        ProjectMemberEntity member = new ProjectMemberEntity();
        member.setProjectId(saved.getId());
        member.setUserId(SecurityUtils.currentUserId());
        member.setProjectRole("PROJECT_ADMIN");
        projectMemberRepository.save(member);
        return toResponse(saved);
    }

    @Transactional
    public ProjectResponse update(String id, UpdateProjectRequest request) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.PROJECT_ADMIN);
        projectAccessService.requireProjectAccess(id);
        ProjectEntity project = require(id);
        if (request.name() != null) {
            project.setName(request.name());
        }
        if (request.status() != null) {
            project.setStatus(request.status());
        }
        if (request.startDate() != null) {
            project.setStartDate(request.startDate());
        }
        if (request.endDate() != null) {
            project.setEndDate(request.endDate());
        }
        if (request.managerUserId() != null) {
            project.setManagerUserId(request.managerUserId());
        }
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse replaceMembers(String id, ReplaceMembersRequest request) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.PROJECT_ADMIN);
        projectAccessService.requireProjectAccess(id);
        require(id);
        projectMemberRepository.deleteByProjectId(id);
        for (ProjectMemberRequest m : request.members()) {
            ProjectMemberEntity entity = new ProjectMemberEntity();
            entity.setProjectId(id);
            entity.setUserId(m.userId());
            entity.setProjectRole(m.projectRole());
            projectMemberRepository.save(entity);
        }
        return toResponse(require(id));
    }

    private ProjectEntity require(String id) {
        return projectRepository.findById(id).orElseThrow(() -> new NotFoundException("Project not found"));
    }

    private ProjectResponse toResponse(ProjectEntity project) {
        List<ProjectMemberResponse> members = projectMemberRepository.findByProjectId(project.getId()).stream()
                .map(m -> new ProjectMemberResponse(m.getId(), m.getUserId(), m.getProjectRole()))
                .toList();
        return new ProjectResponse(
                project.getId(),
                project.getCode(),
                project.getName(),
                project.getStatus(),
                project.getStartDate(),
                project.getEndDate(),
                project.getManagerUserId(),
                project.getVersion(),
                members);
    }
}
