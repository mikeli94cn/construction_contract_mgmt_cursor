package com.company.ccm.shared.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void addsSameCurrency() {
        Money a = Money.of(new BigDecimal("10.00"), "CNY");
        Money b = Money.of(new BigDecimal("2.50"), "CNY");
        assertEquals(new BigDecimal("12.5000"), a.add(b).amount());
    }

    @Test
    void rejectsCurrencyMismatch() {
        Money a = Money.of(new BigDecimal("10.00"), "CNY");
        Money b = Money.of(new BigDecimal("2.50"), "USD");
        assertThrows(IllegalArgumentException.class, () -> a.add(b));
    }
}
