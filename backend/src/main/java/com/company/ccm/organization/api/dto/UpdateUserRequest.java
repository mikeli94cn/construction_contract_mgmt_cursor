package com.company.ccm.organization.api.dto;

public record UpdateUserRequest(String displayName, String email, String phone, String status, String password) {}
