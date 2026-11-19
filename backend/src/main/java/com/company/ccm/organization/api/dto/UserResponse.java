package com.company.ccm.organization.api.dto;

import java.util.List;

public record UserResponse(
        String id,
        String username,
        String displayName,
        String email,
        String phone,
        String status,
        List<String> roles,
        Long version) {}
