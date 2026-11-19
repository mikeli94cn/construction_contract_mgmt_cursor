package com.company.ccm.approval.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "approval_instances")
public class ApprovalInstanceEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "definition_id", nullable = false, length = 36)
    private String definitionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 32)
    private ApprovalTargetType targetType;

    @Column(name = "target_id", nullable = false, length = 36)
    private String targetId;

    @Column(name = "project_id", length = 36)
    private String projectId;

    @Column(nullable = false, length = 32)
    private String status = "IN_PROGRESS";

    @Column(name = "current_step_order", nullable = false)
    private int currentStepOrder = 1;

    @Column(name = "started_by", nullable = false, length = 36)
    private String startedBy;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (startedAt == null) {
            startedAt = Instant.now();
        }
    }

    public String getId() {
        return id;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }

    public ApprovalTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(ApprovalTargetType targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCurrentStepOrder() {
        return currentStepOrder;
    }

    public void setCurrentStepOrder(int currentStepOrder) {
        this.currentStepOrder = currentStepOrder;
    }

    public String getStartedBy() {
        return startedBy;
    }

    public void setStartedBy(String startedBy) {
        this.startedBy = startedBy;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
