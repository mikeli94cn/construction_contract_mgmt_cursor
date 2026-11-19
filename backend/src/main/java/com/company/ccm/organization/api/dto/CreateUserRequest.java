package com.company.ccm.organization.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String displayName,
        String email,
        String phone,
        @NotEmpty List<String> roleCodes) {}
