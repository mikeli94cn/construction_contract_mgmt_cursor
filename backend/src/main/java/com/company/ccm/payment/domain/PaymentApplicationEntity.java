package com.company.ccm.payment.domain;

import com.company.ccm.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;

@Entity
@Table(name = "payment_applications")
public class PaymentApplicationEntity extends BaseEntity {

    @Column(name = "contract_id", nullable = false, length = 36)
    private String contractId;

    @Column(name = "payment_no", nullable = false, unique = true, length = 64)
    private String paymentNo;

    @Column(name = "payment_type", nullable = false, length = 32)
    private String paymentType;

    @Column(name = "period_label", length = 64)
    private String periodLabel;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode = "CNY";

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

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
