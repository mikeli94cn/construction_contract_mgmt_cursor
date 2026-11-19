package com.company.ccm.approval.api.dto;

import com.company.ccm.approval.domain.ApprovalTargetType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public final class ApprovalDtos {
    private ApprovalDtos() {}

    public record ApprovalStepRequest(
            @NotNull Integer stepOrder,
            @NotBlank String name,
            @NotBlank String assigneeType,
            @NotBlank String assigneeValue,
            Integer minApprovals) {}

    public record ApprovalDefinitionRequest(
            @NotBlank String code,
            @NotBlank String name,
            @NotNull ApprovalTargetType targetType,
            Boolean active,
            @NotEmpty @Valid List<ApprovalStepRequest> steps) {}

    public record ApprovalStepResponse(
            String id,
            int stepOrder,
            String name,
            String assigneeType,
            String assigneeValue,
            int minApprovals) {}

    public record ApprovalDefinitionResponse(
            String id,
            String code,
            String name,
            ApprovalTargetType targetType,
            boolean active,
            Long version,
            List<ApprovalStepResponse> steps) {}

    public record ApprovalTaskResponse(
            String id,
            String instanceId,
            ApprovalTargetType targetType,
            String targetId,
            String projectId,
            String status,
            Instant createdAt) {}

    public record ApprovalActionRequest(String comment) {}
}
