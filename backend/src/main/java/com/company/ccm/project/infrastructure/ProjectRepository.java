package com.company.ccm.project.infrastructure;

import com.company.ccm.project.domain.ProjectEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity, String> {
    Optional<ProjectEntity> findByCode(String code);

    boolean existsByCode(String code);
}
