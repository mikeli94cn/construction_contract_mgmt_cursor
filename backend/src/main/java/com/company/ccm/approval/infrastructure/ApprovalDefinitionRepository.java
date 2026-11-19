package com.company.ccm.approval.infrastructure;

import com.company.ccm.approval.domain.ApprovalDefinitionEntity;
import com.company.ccm.approval.domain.ApprovalTargetType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalDefinitionRepository extends JpaRepository<ApprovalDefinitionEntity, String> {
    Optional<ApprovalDefinitionEntity> findByCode(String code);

    Optional<ApprovalDefinitionEntity> findFirstByTargetTypeAndActiveTrueOrderByCreatedAtDesc(
            ApprovalTargetType targetType);

    boolean existsByCode(String code);
}
