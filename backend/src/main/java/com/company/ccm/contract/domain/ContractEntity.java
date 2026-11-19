package com.company.ccm.contract.domain;

import com.company.ccm.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contracts")
public class ContractEntity extends BaseEntity {

    @Column(name = "project_id", nullable = false, length = 36)
    private String projectId;

    @Column(name = "partner_id", nullable = false, length = 36)
    private String partnerId;

    @Column(name = "contract_no", nullable = false, unique = true, length = 64)
    private String contractNo;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 64)
    private String type = "CONSTRUCTION";

    @Column(name = "pricing_type", nullable = false, length = 32)
    private String pricingType;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode = "CNY";

    @Column(name = "original_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal originalAmount;

    @Column(name = "committed_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal committedAmount;

    @Column(nullable = false, length = 32)
    private String status = "DRAFT";

    @Column(name = "signed_date")
    private LocalDate signedDate;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Version
    private Long version;

    @Column(name = "created_by", nullable = false, length = 36)
    private String createdBy;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPricingType() {
        return pricingType;
    }

    public void setPricingType(String pricingType) {
        this.pricingType = pricingType;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public BigDecimal getCommittedAmount() {
        return committedAmount;
    }

    public void setCommittedAmount(BigDecimal committedAmount) {
        this.committedAmount = committedAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(LocalDate signedDate) {
        this.signedDate = signedDate;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    public boolean isSettled() {
        return "SETTLED".equals(status);
    }
}
