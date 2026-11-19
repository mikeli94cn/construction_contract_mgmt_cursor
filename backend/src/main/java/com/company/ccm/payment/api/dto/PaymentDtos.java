package com.company.ccm.payment.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public final class PaymentDtos {
    private PaymentDtos() {}

    public record CreatePaymentRequest(
            @NotBlank String paymentNo,
            @NotBlank String paymentType,
            String periodLabel,
            @NotNull BigDecimal amount,
            String currencyCode) {}

    public record UpdatePaymentRequest(String paymentType, String periodLabel, BigDecimal amount) {}

    public record PaymentResponse(
            String id,
            String contractId,
            String paymentNo,
            String paymentType,
            String periodLabel,
            BigDecimal amount,
            String currencyCode,
            String status,
            Long version,
            String createdBy) {}
}
