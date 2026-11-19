package com.company.ccm.approval.infrastructure;

import com.company.ccm.approval.domain.ApprovalInstanceEntity;
import com.company.ccm.approval.domain.ApprovalTargetType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalInstanceRepository extends JpaRepository<ApprovalInstanceEntity, String> {
    Optional<ApprovalInstanceEntity> findByTargetTypeAndTargetIdAndStatus(
            ApprovalTargetType targetType, String targetId, String status);

    boolean existsByTargetTypeAndTargetIdAndStatus(
            ApprovalTargetType targetType, String targetId, String status);
}
