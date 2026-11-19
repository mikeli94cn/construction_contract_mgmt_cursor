package com.company.ccm.contract.application;

import com.company.ccm.approval.application.ApprovalService;
import com.company.ccm.approval.domain.ApprovalTargetType;
import com.company.ccm.audit.application.AuditService;
import com.company.ccm.contract.api.dto.ContractDtos.ContractResponse;
import com.company.ccm.contract.api.dto.ContractDtos.CreateContractRequest;
import com.company.ccm.contract.api.dto.ContractDtos.UpdateContractRequest;
import com.company.ccm.contract.domain.ContractEntity;
import com.company.ccm.contract.infrastructure.ContractRepository;
import com.company.ccm.organization.domain.RoleCodes;
import com.company.ccm.partner.infrastructure.PartnerRepository;
import com.company.ccm.project.application.ProjectAccessService;
import com.company.ccm.project.infrastructure.ProjectRepository;
import com.company.ccm.shared.domain.BusinessException;
import com.company.ccm.shared.domain.ConflictException;
import com.company.ccm.shared.domain.NotFoundException;
import com.company.ccm.shared.security.SecurityUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContractService {

    private static final Set<String> PRICING = Set.of("LUMP_SUM", "UNIT_PRICE", "COST_PLUS");

    private final ContractRepository contractRepository;
    private final ProjectRepository projectRepository;
    private final PartnerRepository partnerRepository;
    private final ProjectAccessService projectAccessService;
    private final ApprovalService approvalService;
    private final AuditService auditService;

    public ContractService(
            ContractRepository contractRepository,
            ProjectRepository projectRepository,
            PartnerRepository partnerRepository,
            ProjectAccessService projectAccessService,
            @Lazy ApprovalService approvalService,
            AuditService auditService) {
        this.contractRepository = contractRepository;
        this.projectRepository = projectRepository;
        this.partnerRepository = partnerRepository;
        this.projectAccessService = projectAccessService;
        this.approvalService = approvalService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> listByProject(String projectId) {
        projectAccessService.requireProjectAccess(projectId);
        return contractRepository.findByProjectId(projectId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> listAll() {
        return contractRepository.findAll().stream()
                .filter(c -> projectAccessService.canAccessProject(c.getProjectId()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContractResponse get(String id) {
        ContractEntity contract = require(id);
        projectAccessService.requireProjectAccess(contract.getProjectId());
        return toResponse(contract);
    }

    @Transactional
    public ContractResponse create(String projectId, CreateContractRequest request) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.CONTRACT_MANAGER, RoleCodes.PROJECT_ADMIN);
        projectAccessService.requireProjectAccess(projectId);
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException("Project not found");
        }
        if (!partnerRepository.existsById(request.partnerId())) {
            throw new NotFoundException("Partner not found");
        }
        if (!PRICING.contains(request.pricingType())) {
            throw new BusinessException("Invalid pricing type");
        }
        if (contractRepository.existsByContractNo(request.contractNo())) {
            throw new BusinessException("Contract number already exists");
        }
        if (request.originalAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Amount must be non-negative");
        }
        ContractEntity contract = new ContractEntity();
        contract.setProjectId(projectId);
        contract.setPartnerId(request.partnerId());
        contract.setContractNo(request.contractNo());
        contract.setTitle(request.title());
        contract.setType(request.type() == null ? "CONSTRUCTION" : request.type());
        contract.setPricingType(request.pricingType());
        contract.setCurrencyCode(request.currencyCode() == null ? "CNY" : request.currencyCode());
        contract.setOriginalAmount(request.originalAmount());
        contract.setCommittedAmount(request.originalAmount());
        contract.setStatus("DRAFT");
        contract.setSignedDate(request.signedDate());
        contract.setEffectiveDate(request.effectiveDate());
        contract.setEndDate(request.endDate());
        contract.setCreatedBy(SecurityUtils.currentUserId());
        ContractEntity saved = contractRepository.save(contract);
        auditService.record("CONTRACT_CREATED", "CONTRACT", saved.getId(), projectId, Map.of("contractNo", saved.getContractNo()));
        return toResponse(saved);
    }

    @Transactional
    public ContractResponse update(String id, UpdateContractRequest request) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.CONTRACT_MANAGER);
        ContractEntity contract = require(id);
        projectAccessService.requireProjectAccess(contract.getProjectId());
        if (!contract.isEditable()) {
            throw new BusinessException("Approved or non-draft contracts cannot be silently modified");
        }
        if (request.title() != null) {
            contract.setTitle(request.title());
        }
        if (request.type() != null) {
            contract.setType(request.type());
        }
        if (request.pricingType() != null) {
            if (!PRICING.contains(request.pricingType())) {
                throw new BusinessException("Invalid pricing type");
            }
            contract.setPricingType(request.pricingType());
        }
        if (request.originalAmount() != null) {
            if (request.originalAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("Amount must be non-negative");
            }
            contract.setOriginalAmount(request.originalAmount());
            contract.setCommittedAmount(request.originalAmount());
        }
        if (request.signedDate() != null) {
            contract.setSignedDate(request.signedDate());
        }
        if (request.effectiveDate() != null) {
            contract.setEffectiveDate(request.effectiveDate());
        }
        if (request.endDate() != null) {
            contract.setEndDate(request.endDate());
        }
        try {
            return toResponse(contractRepository.save(contract));
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConflictException("Contract was modified by another user");
        }
    }

    @Transactional
    public ContractResponse submit(String id) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.CONTRACT_MANAGER);
        ContractEntity contract = require(id);
        projectAccessService.requireProjectAccess(contract.getProjectId());
        if (!contract.isEditable()) {
            throw new BusinessException("Only DRAFT or REJECTED contracts can be submitted");
        }
        contract.setStatus("PENDING_APPROVAL");
        contractRepository.save(contract);
        approvalService.startApproval(ApprovalTargetType.CONTRACT, contract.getId(), contract.getProjectId());
        return toResponse(contract);
    }

    @Transactional
    public ContractResponse activate(String id) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN, RoleCodes.CONTRACT_MANAGER, RoleCodes.PROJECT_ADMIN);
        ContractEntity contract = require(id);
        projectAccessService.requireProjectAccess(contract.getProjectId());
        if (!"APPROVED".equals(contract.getStatus())) {
            throw new BusinessException("Only APPROVED contracts can be activated");
        }
        contract.setStatus("ACTIVE");
        ContractEntity saved = contractRepository.save(contract);
        auditService.record("CONTRACT_ACTIVATED", "CONTRACT", id, contract.getProjectId(), Map.of());
        return toResponse(saved);
    }

    @Transactional
    public void markApproved(String id) {
        ContractEntity contract = require(id);
        contract.setStatus("APPROVED");
        contractRepository.save(contract);
    }

    @Transactional
    public void markRejected(String id) {
        ContractEntity contract = require(id);
        contract.setStatus("REJECTED");
        contractRepository.save(contract);
    }

    @Transactional
    public void applyCommittedDelta(String contractId, BigDecimal delta) {
        ContractEntity contract = require(contractId);
        if (contract.isSettled()) {
            throw new BusinessException("Settled contracts cannot accept change orders");
        }
        contract.setCommittedAmount(contract.getCommittedAmount().add(delta));
        try {
            contractRepository.save(contract);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConflictException("Contract was modified by another user");
        }
    }

    @Transactional
    public void markSettled(String contractId) {
        ContractEntity contract = require(contractId);
        contract.setStatus("SETTLED");
        contractRepository.save(contract);
    }

    public ContractEntity require(String id) {
        return contractRepository.findById(id).orElseThrow(() -> new NotFoundException("Contract not found"));
    }

    private ContractResponse toResponse(ContractEntity c) {
        return new ContractResponse(
                c.getId(),
                c.getProjectId(),
                c.getPartnerId(),
                c.getContractNo(),
                c.getTitle(),
                c.getType(),
                c.getPricingType(),
                c.getCurrencyCode(),
                c.getOriginalAmount(),
                c.getCommittedAmount(),
                c.getStatus(),
                c.getSignedDate(),
                c.getEffectiveDate(),
                c.getEndDate(),
                c.getVersion(),
                c.getCreatedBy());
    }
}
