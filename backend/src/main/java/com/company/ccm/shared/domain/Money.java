package com.company.ccm.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money {

    private final BigDecimal amount;
    private final String currencyCode;

    private Money(BigDecimal amount, String currencyCode) {
        this.amount = amount.setScale(4, RoundingMode.HALF_UP);
        this.currencyCode = currencyCode;
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currencyCode, "currencyCode");
        if (currencyCode.length() != 3) {
            throw new IllegalArgumentException("currencyCode must be ISO-4217");
        }
        return new Money(amount, currencyCode.toUpperCase());
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return of(amount.add(other.amount), currencyCode);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return of(amount.subtract(other.amount), currencyCode);
    }

    private void requireSameCurrency(Money other) {
        if (!currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
    }

    public BigDecimal amount() {
        return amount;
    }

    public String currencyCode() {
        return currencyCode;
    }
}
