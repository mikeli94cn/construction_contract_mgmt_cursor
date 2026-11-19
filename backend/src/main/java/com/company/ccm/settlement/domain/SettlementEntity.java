package com.company.ccm.settlement.domain;

import com.company.ccm.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "settlements")
public class SettlementEntity extends BaseEntity {

    @Column(name = "contract_id", nullable = false, unique = true, length = 36)
    private String contractId;

    @Column(name = "settlement_no", nullable = false, unique = true, length = 64)
    private String settlementNo;

    @Column(name = "final_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal finalAmount;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode = "CNY";

    @Column(nullable = false, length = 32)
    private String status = "DRAFT";

    @Column(name = "sealed_at")
    private Instant sealedAt;

    @Version
    private Long version;

    @Column(name = "created_by", nullable = false, length = 36)
    private String createdBy;

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getSettlementNo() {
        return settlementNo;
    }

    public void setSettlementNo(String settlementNo) {
        this.settlementNo = settlementNo;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getSealedAt() {
        return sealedAt;
    }

    public void setSealedAt(Instant sealedAt) {
        this.sealedAt = sealedAt;
    }

    public Long getVersion() {
        return version;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isEditable() {
        return "DRAFT".equals(status) || "REJECTED".equals(status);
    }
}
