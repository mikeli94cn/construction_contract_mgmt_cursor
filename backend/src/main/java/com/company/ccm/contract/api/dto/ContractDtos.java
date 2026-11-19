package com.company.ccm.contract.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class ContractDtos {
    private ContractDtos() {}

    public record CreateContractRequest(
            @NotBlank String partnerId,
            @NotBlank String contractNo,
            @NotBlank String title,
            String type,
            @NotBlank String pricingType,
            String currencyCode,
            @NotNull BigDecimal originalAmount,
            LocalDate signedDate,
            LocalDate effectiveDate,
            LocalDate endDate) {}

    public record UpdateContractRequest(
            String title,
            String type,
            String pricingType,
            BigDecimal originalAmount,
            LocalDate signedDate,
            LocalDate effectiveDate,
            LocalDate endDate) {}

    public record ContractResponse(
            String id,
            String projectId,
            String partnerId,
            String contractNo,
            String title,
            String type,
            String pricingType,
            String currencyCode,
            BigDecimal originalAmount,
            BigDecimal committedAmount,
            String status,
            LocalDate signedDate,
            LocalDate effectiveDate,
            LocalDate endDate,
            Long version,
            String createdBy) {}
}
