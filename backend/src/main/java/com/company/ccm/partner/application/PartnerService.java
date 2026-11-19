package com.company.ccm.partner.application;

import com.company.ccm.organization.domain.RoleCodes;
import com.company.ccm.partner.api.dto.PartnerDtos.CreatePartnerRequest;
import com.company.ccm.partner.api.dto.PartnerDtos.PartnerResponse;
import com.company.ccm.partner.api.dto.PartnerDtos.UpdatePartnerRequest;
import com.company.ccm.partner.domain.PartnerEntity;
import com.company.ccm.partner.infrastructure.PartnerRepository;
import com.company.ccm.shared.domain.BusinessException;
import com.company.ccm.shared.domain.NotFoundException;
import com.company.ccm.shared.security.SecurityUtils;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartnerService {

    private static final Set<String> TYPES = Set.of("CLIENT", "CONTRACTOR", "SUPPLIER", "OTHER");

    private final PartnerRepository partnerRepository;

    public PartnerService(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Transactional(readOnly = true)
    public List<PartnerResponse> list() {
        return partnerRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PartnerResponse get(String id) {
        return toResponse(require(id));
    }

    @Transactional
    public PartnerResponse create(CreatePartnerRequest request) {
        SecurityUtils.requireAnyRole(
                RoleCodes.SYSTEM_ADMIN, RoleCodes.PROJECT_ADMIN, RoleCodes.CONTRACT_MANAGER);
        validateType(request.type());
        if (partnerRepository.existsByCode(request.code())) {
            throw new BusinessException("Partner code already exists");
        }
        PartnerEntity partner = new PartnerEntity();
        partner.setCode(request.code());
        partner.setName(request.name());
        partner.setType(request.type());
        partner.setContactName(request.contactName());
        partner.setContactPhone(request.contactPhone());
        partner.setTaxId(request.taxId());
        partner.setBankName(request.bankName());
        partner.setBankAccount(request.bankAccount());
        partner.setStatus("ACTIVE");
        return toResponse(partnerRepository.save(partner));
    }

    @Transactional
    public PartnerResponse update(String id, UpdatePartnerRequest request) {
        SecurityUtils.requireAnyRole(
                RoleCodes.SYSTEM_ADMIN, RoleCodes.PROJECT_ADMIN, RoleCodes.CONTRACT_MANAGER);
        PartnerEntity partner = require(id);
        if (request.name() != null) {
            partner.setName(request.name());
        }
        if (request.type() != null) {
            validateType(request.type());
            partner.setType(request.type());
        }
        if (request.contactName() != null) {
            partner.setContactName(request.contactName());
        }
        if (request.contactPhone() != null) {
            partner.setContactPhone(request.contactPhone());
        }
        if (request.taxId() != null) {
            partner.setTaxId(request.taxId());
        }
        if (request.bankName() != null) {
            partner.setBankName(request.bankName());
        }
        if (request.bankAccount() != null) {
            partner.setBankAccount(request.bankAccount());
        }
        if (request.status() != null) {
            partner.setStatus(request.status());
        }
        return toResponse(partnerRepository.save(partner));
    }

    private void validateType(String type) {
        if (!TYPES.contains(type)) {
            throw new BusinessException("Invalid partner type");
        }
    }

    private PartnerEntity require(String id) {
        return partnerRepository.findById(id).orElseThrow(() -> new NotFoundException("Partner not found"));
    }

    private PartnerResponse toResponse(PartnerEntity partner) {
        return new PartnerResponse(
                partner.getId(),
                partner.getCode(),
                partner.getName(),
                partner.getType(),
                partner.getContactName(),
                partner.getContactPhone(),
                partner.getTaxId(),
                partner.getBankName(),
                partner.getBankAccount(),
                partner.getStatus(),
                partner.getVersion());
    }
}
