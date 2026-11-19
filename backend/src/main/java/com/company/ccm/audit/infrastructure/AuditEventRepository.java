package com.company.ccm.audit.infrastructure;

import com.company.ccm.audit.domain.AuditEventEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, String> {
    List<AuditEventEntity> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, String entityId);

    List<AuditEventEntity> findByProjectIdOrderByCreatedAtDesc(String projectId);

    List<AuditEventEntity> findTop100ByOrderByCreatedAtDesc();
}
