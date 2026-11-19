package com.company.ccm.changeorder.application;

import com.company.ccm.approval.application.ApprovalService;
import com.company.ccm.approval.domain.ApprovalTargetType;
import com.company.ccm.audit.application.AuditService;
import com.company.ccm.changeorder.api.dto.ChangeOrderDtos.ChangeOrderResponse;
import com.company.ccm.changeorder.api.dto.ChangeOrderDtos.CreateChangeOrderRequest;
import com.company.ccm.changeorder.api.dto.ChangeOrderDtos.UpdateChangeOrderRequest;
import com.company.ccm.changeorder.domain.ChangeOrderEntity;
import com.company.ccm.changeorder.infrastructure.ChangeOrderRepository;
import com.company.ccm.contract.application.ContractService;
import com.company.ccm.contract.domain.ContractEntity;
import com.company.ccm.organization.domain.RoleCodes;
import com.company.ccm.project.application.ProjectAccessService;
import com.company.ccm.shared.domain.BusinessException;
import com.company.ccm.shared.domain.NotFoundException;
import com.company.ccm.shared.security.SecurityUtils;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeOrderService {

    private final ChangeOrderRepository changeOrderRepository;
    private final ContractService contractService;
    private final ProjectAccessService projectAccessService;
    private final ApprovalService approvalService;
    private final AuditService auditService;

    public ChangeOrderService(
            ChangeOrderRepository changeOrderRepository,
            ContractService contractService,
            ProjectAccessService projectAccessService,
            @Lazy ApprovalService approvalService,
            AuditService auditService) {
        this.changeOrderRepository = changeOrderRepository;
        this.contractService = contractService;
        this.projectAccessService = projectAccessService;
        this.approvalService = approvalService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<ChangeOrderResponse> listByContract(String contractId) {
        ContractEntity contract = contractService.require(contractId);
        projectAccessService.requireProjectAccess(contract.getProjectId());
        return changeOrderRepository.findByContractId(contractId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ChangeOrderResponse get(String id) {
        ChangeOrderEntity co = require(id);
        ContractEntity contract = contractService.require(co.getContractId());
        projectAccessService.requireProjectAccess(contract.getProjectId());
        return toResponse(co);
    }

    @Transactional
    public ChangeOrderResponse create(String contractId, CreateChangeOrderRequest request) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.CONTRACT_MANAGER);
        ContractEntity contract = contractService.require(contractId);
        projectAccessService.requireProjectAccess(contract.getProjectId());
        if (contract.isSettled() || "TERMINATED".equals(contract.getStatus())) {
            throw new BusinessException("Cannot create change order on settled/terminated contract");
        }
        if (!"ACTIVE".equals(contract.getStatus()) && !"APPROVED".equals(contract.getStatus())) {
            throw new BusinessException("Contract must be ACTIVE or APPROVED");
        }
        if (changeOrderRepository.existsByChangeNo(request.changeNo())) {
            throw new BusinessException("Change order number already exists");
        }
        ChangeOrderEntity co = new ChangeOrderEntity();
        co.setContractId(contractId);
        co.setChangeNo(request.changeNo());
        co.setTitle(request.title());
        co.setAmountDelta(request.amountDelta());
        co.setCurrencyCode(request.currencyCode() == null ? contract.getCurrencyCode() : request.currencyCode());
        co.setReason(request.reason());
        co.setStatus("DRAFT");
        co.setCreatedBy(SecurityUtils.currentUserId());
        return toResponse(changeOrderRepository.save(co));
    }

    @Transactional
    public ChangeOrderResponse update(String id, UpdateChangeOrderRequest request) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.CONTRACT_MANAGER);
        ChangeOrderEntity co = require(id);
        ContractEntity contract = contractService.require(co.getContractId());
        projectAccessService.requireProjectAccess(contract.getProjectId());
        if (!co.isEditable()) {
            throw new BusinessException("Approved change orders cannot be silently modified");
        }
        if (request.title() != null) {
            co.setTitle(request.title());
        }
        if (request.amountDelta() != null) {
            co.setAmountDelta(request.amountDelta());
        }
        if (request.reason() != null) {
            co.setReason(request.reason());
        }
        return toResponse(changeOrderRepository.save(co));
    }

    @Transactional
    public ChangeOrderResponse submit(String id) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.CONTRACT_MANAGER);
        ChangeOrderEntity co = require(id);
        ContractEntity contract = contractService.require(co.getContractId());
        projectAccessService.requireProjectAccess(contract.getProjectId());
        if (!co.isEditable()) {
            throw new BusinessException("Only DRAFT or REJECTED change orders can be submitted");
        }
        co.setStatus("PENDING_APPROVAL");
        changeOrderRepository.save(co);
        approvalService.startApproval(ApprovalTargetType.CHANGE_ORDER, co.getId(), contract.getProjectId());
        return toResponse(co);
    }

    @Transactional
    public void markApproved(String id) {
        ChangeOrderEntity co = require(id);
        co.setStatus("APPROVED");
        changeOrderRepository.save(co);
        contractService.applyCommittedDelta(co.getContractId(), co.getAmountDelta());
        auditService.record(
                "CHANGE_ORDER_APPROVED",
                "CHANGE_ORDER",
                id,
                contractService.require(co.getContractId()).getProjectId(),
                Map.of("amountDelta", co.getAmountDelta()));
    }

    @Transactional
    public void markRejected(String id) {
        ChangeOrderEntity co = require(id);
        co.setStatus("REJECTED");
        changeOrderRepository.save(co);
    }

    private ChangeOrderEntity require(String id) {
        return changeOrderRepository.findById(id).orElseThrow(() -> new NotFoundException("Change order not found"));
    }

    private ChangeOrderResponse toResponse(ChangeOrderEntity co) {
        return new ChangeOrderResponse(
                co.getId(),
                co.getContractId(),
                co.getChangeNo(),
                co.getTitle(),
                co.getAmountDelta(),
                co.getCurrencyCode(),
                co.getReason(),
                co.getStatus(),
                co.getVersion(),
                co.getCreatedBy());
    }
}
