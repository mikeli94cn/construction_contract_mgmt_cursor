package com.company.ccm.audit.api;

import com.company.ccm.contract.infrastructure.ContractRepository;
import com.company.ccm.payment.infrastructure.PaymentApplicationRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ContractRepository contractRepository;
    private final PaymentApplicationRepository paymentApplicationRepository;

    public ReportController(
            ContractRepository contractRepository, PaymentApplicationRepository paymentApplicationRepository) {
        this.contractRepository = contractRepository;
        this.paymentApplicationRepository = paymentApplicationRepository;
    }

    @GetMapping("/contract-summary")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','FINANCE','PROJECT_ADMIN','CONTRACT_MANAGER')")
    public List<Map<String, Object>> contractSummary() {
        return contractRepository.findAll().stream()
                .map(c -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("contractId", c.getId());
                    row.put("contractNo", c.getContractNo());
                    row.put("projectId", c.getProjectId());
                    row.put("status", c.getStatus());
                    row.put("currencyCode", c.getCurrencyCode());
                    row.put("originalAmount", c.getOriginalAmount());
                    row.put("committedAmount", c.getCommittedAmount());
                    BigDecimal paid = paymentApplicationRepository.sumApprovedOrPaid(c.getId());
                    row.put("paidAmount", paid);
                    row.put("remainingAmount", c.getCommittedAmount().subtract(paid));
                    return row;
                })
                .toList();
    }
}
