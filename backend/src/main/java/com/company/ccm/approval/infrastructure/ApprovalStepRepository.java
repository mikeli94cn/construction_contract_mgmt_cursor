package com.company.ccm.approval.infrastructure;

import com.company.ccm.approval.domain.ApprovalStepEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalStepRepository extends JpaRepository<ApprovalStepEntity, String> {
    List<ApprovalStepEntity> findByDefinitionIdOrderByStepOrderAsc(String definitionId);

    void deleteByDefinitionId(String definitionId);

    Optional<ApprovalStepEntity> findByDefinitionIdAndStepOrder(String definitionId, int stepOrder);
}
