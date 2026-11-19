package com.company.ccm.audit.api;

import com.company.ccm.audit.application.AuditService;
import com.company.ccm.audit.domain.AuditEventEntity;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit-events")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','FINANCE','PROJECT_ADMIN')")
    public List<AuditEventEntity> list(@RequestParam(required = false) String projectId) {
        if (projectId != null && !projectId.isBlank()) {
            return auditService.byProject(projectId);
        }
        return auditService.recent();
    }
}
