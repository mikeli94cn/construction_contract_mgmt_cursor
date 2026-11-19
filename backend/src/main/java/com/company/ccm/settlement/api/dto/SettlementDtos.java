package com.company.ccm.settlement.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public final class SettlementDtos {
    private SettlementDtos() {}

    public record CreateSettlementRequest(
            @NotBlank String settlementNo, @NotNull BigDecimal finalAmount, String currencyCode) {}

    public record SettlementResponse(
            String id,
            String contractId,
            String settlementNo,
            BigDecimal finalAmount,
            String currencyCode,
            String status,
            Instant sealedAt,
            Long version,
            String createdBy) {}
}
