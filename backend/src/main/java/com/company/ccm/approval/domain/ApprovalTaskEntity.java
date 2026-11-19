package com.company.ccm.approval.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "approval_tasks")
public class ApprovalTaskEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "instance_id", nullable = false, length = 36)
    private String instanceId;

    @Column(name = "step_id", nullable = false, length = 36)
    private String stepId;

    @Column(name = "assignee_user_id", nullable = false, length = 36)
    private String assigneeUserId;

    @Column(nullable = false, length = 32)
    private String status = "PENDING";

    @Column(length = 1000)
    private String comment;

    @Column(name = "acted_at")
    private Instant actedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public String getId() {
        return id;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getAssigneeUserId() {
        return assigneeUserId;
    }

    public void setAssigneeUserId(String assigneeUserId) {
        this.assigneeUserId = assigneeUserId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getActedAt() {
        return actedAt;
    }

    public void setActedAt(Instant actedAt) {
        this.actedAt = actedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
