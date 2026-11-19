package com.company.ccm.changeorder.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public final class ChangeOrderDtos {
    private ChangeOrderDtos() {}

    public record CreateChangeOrderRequest(
            @NotBlank String changeNo,
            @NotBlank String title,
            @NotNull BigDecimal amountDelta,
            String currencyCode,
            String reason) {}

    public record UpdateChangeOrderRequest(String title, BigDecimal amountDelta, String reason) {}

    public record ChangeOrderResponse(
            String id,
            String contractId,
            String changeNo,
            String title,
            BigDecimal amountDelta,
            String currencyCode,
            String reason,
            String status,
            Long version,
            String createdBy) {}
}
