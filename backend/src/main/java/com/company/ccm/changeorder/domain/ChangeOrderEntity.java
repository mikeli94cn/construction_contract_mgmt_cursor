package com.company.ccm.changeorder.domain;

import com.company.ccm.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;

@Entity
@Table(name = "change_orders")
public class ChangeOrderEntity extends BaseEntity {

    @Column(name = "contract_id", nullable = false, length = 36)
    private String contractId;

    @Column(name = "change_no", nullable = false, unique = true, length = 64)
    private String changeNo;

    @Column(nullable = false)
    private String title;

    @Column(name = "amount_delta", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountDelta;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode = "CNY";

    @Column(length = 1000)
    private String reason;

    @Column(nullable = false, length = 32)
    private String status = "DRAFT";

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

    public String getChangeNo() {
        return changeNo;
    }

    public void setChangeNo(String changeNo) {
        this.changeNo = changeNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmountDelta() {
        return amountDelta;
    }

    public void setAmountDelta(BigDecimal amountDelta) {
        this.amountDelta = amountDelta;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
