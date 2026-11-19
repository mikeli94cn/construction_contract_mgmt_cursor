package com.company.ccm.approval.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "approval_steps")
public class ApprovalStepEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "definition_id", nullable = false, length = 36)
    private String definitionId;

    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(name = "assignee_type", nullable = false, length = 32)
    private String assigneeType;

    @Column(name = "assignee_value", nullable = false, length = 128)
    private String assigneeValue;

    @Column(name = "min_approvals", nullable = false)
    private int minApprovals = 1;

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
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

    public int getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssigneeType() {
        return assigneeType;
    }

    public void setAssigneeType(String assigneeType) {
        this.assigneeType = assigneeType;
    }

    public String getAssigneeValue() {
        return assigneeValue;
    }

    public void setAssigneeValue(String assigneeValue) {
        this.assigneeValue = assigneeValue;
    }

    public int getMinApprovals() {
        return minApprovals;
    }

    public void setMinApprovals(int minApprovals) {
        this.minApprovals = minApprovals;
    }
}
