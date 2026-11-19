package com.company.ccm.settlement.application;

import com.company.ccm.approval.application.ApprovalService;
import com.company.ccm.approval.domain.ApprovalTargetType;
import com.company.ccm.audit.application.AuditService;
import com.company.ccm.contract.application.ContractService;
import com.company.ccm.contract.domain.ContractEntity;
import com.company.ccm.organization.domain.RoleCodes;
import com.company.ccm.payment.application.PaymentService;
import com.company.ccm.project.application.ProjectAccessService;
import com.company.ccm.settlement.api.dto.SettlementDtos.CreateSettlementRequest;
import com.company.ccm.settlement.api.dto.SettlementDtos.SettlementResponse;
import com.company.ccm.settlement.domain.SettlementEntity;
import com.company.ccm.settlement.infrastructure.SettlementRepository;
import com.company.ccm.shared.domain.BusinessException;
import com.company.ccm.shared.domain.NotFoundException;
import com.company.ccm.shared.security.SecurityUtils;
import java.time.Instant;
import java.util.Map;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final ContractService contractService;
    private final PaymentService paymentService;
    private final ProjectAccessService projectAccessService;
    private final ApprovalService approvalService;
    private final AuditService auditService;

    public SettlementService(
            SettlementRepository settlementRepository,
            ContractService contractService,
            PaymentService paymentService,
            ProjectAccessService projectAccessService,
            @Lazy ApprovalService approvalService,
            AuditService auditService) {
        this.settlementRepository = settlementRepository;
        this.contractService = contractService;
        this.paymentService = paymentService;
        this.projectAccessService = projectAccessService;
        this.approvalService = approvalService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public SettlementResponse getByContract(String contractId) {
        ContractEntity contract = contractService.require(contractId);
        projectAccessService.requireProjectAccess(contract.getProjectId());
        return settlementRepository
                .findByContractId(contractId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Settlement not found"));
    }

    @Transactional
    public SettlementResponse create(String contractId, CreateSettlementRequest request) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.FINANCE);
        ContractEntity contract = contractService.require(contractId);
        projectAccessService.requireProjectAccess(contract.getProjectId());
        if (contract.isSettled()) {
            throw new BusinessException("Contract already settled");
        }
        if (settlementRepository.existsByContractId(contractId)) {
            throw new BusinessException("Settlement already exists for contract");
        }
        if (settlementRepository.existsBySettlementNo(request.settlementNo())) {
            throw new BusinessException("Settlement number already exists");
        }
        SettlementEntity settlement = new SettlementEntity();
        settlement.setContractId(contractId);
        settlement.setSettlementNo(request.settlementNo());
        settlement.setFinalAmount(request.finalAmount());
        settlement.setCurrencyCode(
                request.currencyCode() == null ? contract.getCurrencyCode() : request.currencyCode());
        settlement.setStatus("DRAFT");
        settlement.setCreatedBy(SecurityUtils.currentUserId());
        return toResponse(settlementRepository.save(settlement));
    }

    @Transactional
    public SettlementResponse submit(String id) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.FINANCE);
        SettlementEntity settlement = require(id);
        ContractEntity contract = contractService.require(settlement.getContractId());
        projectAccessService.requireProjectAccess(contract.getProjectId());
        if (!settlement.isEditable()) {
            throw new BusinessException("Only DRAFT or REJECTED settlements can be submitted");
        }
        settlement.setStatus("PENDING_APPROVAL");
        settlementRepository.save(settlement);
        approvalService.startApproval(ApprovalTargetType.SETTLEMENT, settlement.getId(), contract.getProjectId());
        return toResponse(settlement);
    }

    @Transactional
    public SettlementResponse seal(String id) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.FINANCE);
        SettlementEntity settlement = require(id);
        if (!"APPROVED".equals(settlement.getStatus())) {
            throw new BusinessException("Only APPROVED settlements can be sealed");
        }
        settlement.setStatus("SEALED");
        settlement.setSealedAt(Instant.now());
        settlementRepository.save(settlement);
        contractService.markSettled(settlement.getContractId());
        ContractEntity contract = contractService.require(settlement.getContractId());
        auditService.record(
                "SETTLEMENT_SEALED",
                "SETTLEMENT",
                id,
                contract.getProjectId(),
                Map.of(
                        "finalAmount",
                        settlement.getFinalAmount(),
                        "paidTotal",
                        paymentService.totalApproved(settlement.getContractId())));
        return toResponse(settlement);
    }

    @Transactional
    public void markApproved(String id) {
        SettlementEntity settlement = require(id);
        settlement.setStatus("APPROVED");
        settlementRepository.save(settlement);
    }

    @Transactional
    public void markRejected(String id) {
        SettlementEntity settlement = require(id);
        settlement.setStatus("REJECTED");
        settlementRepository.save(settlement);
    }

    private SettlementEntity require(String id) {
        return settlementRepository.findById(id).orElseThrow(() -> new NotFoundException("Settlement not found"));
    }

    private SettlementResponse toResponse(SettlementEntity s) {
        return new SettlementResponse(
                s.getId(),
                s.getContractId(),
                s.getSettlementNo(),
                s.getFinalAmount(),
                s.getCurrencyCode(),
                s.getStatus(),
                s.getSealedAt(),
                s.getVersion(),
                s.getCreatedBy());
    }
}
