package com.company.ccm.settlement.infrastructure;

import com.company.ccm.settlement.domain.SettlementEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<SettlementEntity, String> {
    Optional<SettlementEntity> findByContractId(String contractId);

    boolean existsBySettlementNo(String settlementNo);

    boolean existsByContractId(String contractId);
}
