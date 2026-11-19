package com.company.ccm.project.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

public final class ProjectDtos {
    private ProjectDtos() {}

    public record CreateProjectRequest(
            @NotBlank String code,
            @NotBlank String name,
            LocalDate startDate,
            LocalDate endDate,
            String managerUserId) {}

    public record UpdateProjectRequest(
            String name, String status, LocalDate startDate, LocalDate endDate, String managerUserId) {}

    public record ProjectMemberRequest(@NotBlank String userId, @NotBlank String projectRole) {}

    public record ReplaceMembersRequest(@NotEmpty List<ProjectMemberRequest> members) {}

    public record ProjectMemberResponse(String id, String userId, String projectRole) {}

    public record ProjectResponse(
            String id,
            String code,
            String name,
            String status,
            LocalDate startDate,
            LocalDate endDate,
            String managerUserId,
            Long version,
            List<ProjectMemberResponse> members) {}
}
