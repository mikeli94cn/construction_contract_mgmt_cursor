package com.company.ccm.partner.infrastructure;

import com.company.ccm.partner.domain.PartnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<PartnerEntity, String> {
    boolean existsByCode(String code);
}
