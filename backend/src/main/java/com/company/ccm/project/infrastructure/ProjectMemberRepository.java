package com.company.ccm.project.infrastructure;

import com.company.ccm.project.domain.ProjectMemberEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, String> {
    List<ProjectMemberEntity> findByProjectId(String projectId);

    List<ProjectMemberEntity> findByUserId(String userId);

    Optional<ProjectMemberEntity> findByProjectIdAndUserId(String projectId, String userId);

    List<ProjectMemberEntity> findByProjectIdAndProjectRole(String projectId, String projectRole);

    void deleteByProjectId(String projectId);

    boolean existsByProjectIdAndUserId(String projectId, String userId);
}
