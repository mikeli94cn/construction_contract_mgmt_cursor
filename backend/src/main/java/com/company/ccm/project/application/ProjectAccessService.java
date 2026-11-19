package com.company.ccm.project.application;

import com.company.ccm.organization.domain.RoleCodes;
import com.company.ccm.project.infrastructure.ProjectMemberRepository;
import com.company.ccm.shared.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ProjectAccessService {

    private final ProjectMemberRepository projectMemberRepository;

    public ProjectAccessService(ProjectMemberRepository projectMemberRepository) {
        this.projectMemberRepository = projectMemberRepository;
    }

    public boolean canAccessProject(String projectId) {
        if (SecurityUtils.hasRole(RoleCodes.SYSTEM_ADMIN)) {
            return true;
        }
        return projectMemberRepository.existsByProjectIdAndUserId(projectId, SecurityUtils.currentUserId());
    }

    public void requireProjectAccess(String projectId) {
        if (!canAccessProject(projectId)) {
            throw new AccessDeniedException("No access to project");
        }
    }
}
