package com.company.ccm.audit.application;

import com.company.ccm.audit.domain.AuditEventEntity;
import com.company.ccm.audit.infrastructure.AuditEventRepository;
import com.company.ccm.shared.security.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditEventRepository auditEventRepository, ObjectMapper objectMapper) {
        this.auditEventRepository = auditEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void record(String action, String entityType, String entityId, String projectId, Map<String, Object> detail) {
        AuditEventEntity event = new AuditEventEntity();
        try {
            event.setActorUserId(SecurityUtils.currentUserId());
        } catch (Exception ignored) {
            event.setActorUserId(null);
        }
        event.setAction(action);
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setProjectId(projectId);
        if (detail != null) {
            try {
                event.setDetailJson(objectMapper.writeValueAsString(detail));
            } catch (JsonProcessingException e) {
                event.setDetailJson("{}");
            }
        }
        auditEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<AuditEventEntity> recent() {
        return auditEventRepository.findTop100ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<AuditEventEntity> byProject(String projectId) {
        return auditEventRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }
}
