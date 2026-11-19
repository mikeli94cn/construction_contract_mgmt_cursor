package com.company.ccm.partner.api.dto;

import jakarta.validation.constraints.NotBlank;

public final class PartnerDtos {
    private PartnerDtos() {}

    public record CreatePartnerRequest(
            @NotBlank String code,
            @NotBlank String name,
            @NotBlank String type,
            String contactName,
            String contactPhone,
            String taxId,
            String bankName,
            String bankAccount) {}

    public record UpdatePartnerRequest(
            String name,
            String type,
            String contactName,
            String contactPhone,
            String taxId,
            String bankName,
            String bankAccount,
            String status) {}

    public record PartnerResponse(
            String id,
            String code,
            String name,
            String type,
            String contactName,
            String contactPhone,
            String taxId,
            String bankName,
            String bankAccount,
            String status,
            Long version) {}
}
