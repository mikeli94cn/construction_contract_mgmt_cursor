package com.company.ccm.organization.api.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AssignRolesRequest(@NotEmpty List<String> roleCodes) {}
