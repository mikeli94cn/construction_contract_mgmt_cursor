package com.company.ccm.approval.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "approval_actions")
public class ApprovalActionEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "instance_id", nullable = false, length = 36)
    private String instanceId;

    @Column(name = "task_id", length = 36)
    private String taskId;

    @Column(name = "actor_user_id", nullable = false, length = 36)
    private String actorUserId;

    @Column(nullable = false, length = 32)
    private String action;

    @Column(length = 1000)
    private String comment;

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

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setActorUserId(String actorUserId) {
        this.actorUserId = actorUserId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
