package com.company.ccm.payment.application;

import com.company.ccm.approval.application.ApprovalService;
import com.company.ccm.approval.domain.ApprovalTargetType;
import com.company.ccm.audit.application.AuditService;
import com.company.ccm.contract.application.ContractService;
import com.company.ccm.contract.domain.ContractEntity;
import com.company.ccm.organization.domain.RoleCodes;
import com.company.ccm.payment.api.dto.PaymentDtos.CreatePaymentRequest;
import com.company.ccm.payment.api.dto.PaymentDtos.PaymentResponse;
import com.company.ccm.payment.api.dto.PaymentDtos.UpdatePaymentRequest;
import com.company.ccm.payment.domain.PaymentApplicationEntity;
import com.company.ccm.payment.infrastructure.PaymentApplicationRepository;
import com.company.ccm.project.application.ProjectAccessService;
import com.company.ccm.shared.domain.BusinessException;
import com.company.ccm.shared.domain.NotFoundException;
import com.company.ccm.shared.security.SecurityUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private static final Set<String> TYPES =
            Set.of("ADVANCE", "PROGRESS", "RETENTION_RELEASE", "FINAL", "OTHER");

    private final PaymentApplicationRepository paymentRepository;
    private final ContractService contractService;
    private final ProjectAccessService projectAccessService;
    private final ApprovalService approvalService;
    private final AuditService auditService;

    public PaymentService(
            PaymentApplicationRepository paymentRepository,
            ContractService contractService,
            ProjectAccessService projectAccessService,
            @Lazy ApprovalService approvalService,
            AuditService auditService) {
        this.paymentRepository = paymentRepository;
        this.contractService = contractService;
        this.projectAccessService = projectAccessService;
        this.approvalService = approvalService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listByContract(String contractId) {
        ContractEntity contract = contractService.require(contractId);
        projectAccessService.requireProjectAccess(contract.getProjectId());
        return paymentRepository.findByContractId(contractId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse get(String id) {
        PaymentApplicationEntity payment = require(id);
        ContractEntity contract = contractService.require(payment.getContractId());
        projectAccessService.requireProjectAccess(contract.getProjectId());
        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse create(String contractId, CreatePaymentRequest request) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.FINANCE, RoleCodes.CONTRACT_MANAGER);
        ContractEntity contract = contractService.require(contractId);
        projectAccessService.requireProjectAccess(contract.getProjectId());
        validateContractForPayment(contract);
        if (!TYPES.contains(request.paymentType())) {
            throw new BusinessException("Invalid payment type");
        }
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Payment amount must be positive");
        }
        if (paymentRepository.existsByPaymentNo(request.paymentNo())) {
            throw new BusinessException("Payment number already exists");
        }
        assertNoOverpayment(contract, request.amount());
        PaymentApplicationEntity payment = new PaymentApplicationEntity();
        payment.setContractId(contractId);
        payment.setPaymentNo(request.paymentNo());
        payment.setPaymentType(request.paymentType());
        payment.setPeriodLabel(request.periodLabel());
        payment.setAmount(request.amount());
        payment.setCurrencyCode(request.currencyCode() == null ? contract.getCurrencyCode() : request.currencyCode());
        payment.setStatus("DRAFT");
        payment.setCreatedBy(SecurityUtils.currentUserId());
        return toResponse(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponse update(String id, UpdatePaymentRequest request) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.FINANCE);
        PaymentApplicationEntity payment = require(id);
        ContractEntity contract = contractService.require(payment.getContractId());
        projectAccessService.requireProjectAccess(contract.getProjectId());
        if (!payment.isEditable()) {
            throw new BusinessException("Approved payments cannot be silently modified");
        }
        if (request.paymentType() != null) {
            if (!TYPES.contains(request.paymentType())) {
                throw new BusinessException("Invalid payment type");
            }
            payment.setPaymentType(request.paymentType());
        }
        if (request.periodLabel() != null) {
            payment.setPeriodLabel(request.periodLabel());
        }
        if (request.amount() != null) {
            if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("Payment amount must be positive");
            }
            assertNoOverpayment(contract, request.amount());
            payment.setAmount(request.amount());
        }
        return toResponse(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponse submit(String id) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.FINANCE);
        PaymentApplicationEntity payment = require(id);
        ContractEntity contract = contractService.require(payment.getContractId());
        projectAccessService.requireProjectAccess(contract.getProjectId());
        validateContractForPayment(contract);
        if (!payment.isEditable()) {
            throw new BusinessException("Only DRAFT or REJECTED payments can be submitted");
        }
        assertNoOverpayment(contract, payment.getAmount());
        payment.setStatus("PENDING_APPROVAL");
        paymentRepository.save(payment);
        approvalService.startApproval(ApprovalTargetType.PAYMENT, payment.getId(), contract.getProjectId());
        return toResponse(payment);
    }

    @Transactional
    public void markApproved(String id) {
        PaymentApplicationEntity payment = require(id);
        ContractEntity contract = contractService.require(payment.getContractId());
        assertNoOverpaymentExcluding(contract, payment.getAmount(), payment.getId());
        payment.setStatus("APPROVED");
        paymentRepository.save(payment);
        auditService.record(
                "PAYMENT_APPROVED",
                "PAYMENT",
                id,
                contract.getProjectId(),
                Map.of("amount", payment.getAmount()));
    }

    @Transactional
    public void markRejected(String id) {
        PaymentApplicationEntity payment = require(id);
        payment.setStatus("REJECTED");
        paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalApproved(String contractId) {
        return paymentRepository.sumApprovedOrPaid(contractId);
    }

    private void validateContractForPayment(ContractEntity contract) {
        if (contract.isSettled()) {
            throw new BusinessException("Cannot pay a settled contract");
        }
        if (!"ACTIVE".equals(contract.getStatus()) && !"APPROVED".equals(contract.getStatus())) {
            throw new BusinessException("Contract must be ACTIVE or APPROVED for payments");
        }
    }

    private void assertNoOverpayment(ContractEntity contract, BigDecimal amount) {
        BigDecimal reserved = paymentRepository.sumReservedOrPaid(contract.getId());
        if (reserved.add(amount).compareTo(contract.getCommittedAmount()) > 0) {
            throw new BusinessException("Payment would exceed committed contract amount");
        }
    }

    private void assertNoOverpaymentExcluding(ContractEntity contract, BigDecimal amount, String excludeId) {
        BigDecimal reserved = paymentRepository.findByContractId(contract.getId()).stream()
                .filter(p -> !p.getId().equals(excludeId))
                .filter(p -> Set.of("APPROVED", "PAID", "PENDING_APPROVAL").contains(p.getStatus()))
                .map(PaymentApplicationEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (reserved.add(amount).compareTo(contract.getCommittedAmount()) > 0) {
            throw new BusinessException("Payment would exceed committed contract amount");
        }
    }

    private PaymentApplicationEntity require(String id) {
        return paymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Payment not found"));
    }

    private PaymentResponse toResponse(PaymentApplicationEntity p) {
        return new PaymentResponse(
                p.getId(),
                p.getContractId(),
                p.getPaymentNo(),
                p.getPaymentType(),
                p.getPeriodLabel(),
                p.getAmount(),
                p.getCurrencyCode(),
                p.getStatus(),
                p.getVersion(),
                p.getCreatedBy());
    }
}
