package com.company.ccm.approval.infrastructure;

import com.company.ccm.approval.domain.ApprovalTaskEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalTaskRepository extends JpaRepository<ApprovalTaskEntity, String> {
    List<ApprovalTaskEntity> findByAssigneeUserIdAndStatus(String assigneeUserId, String status);

    List<ApprovalTaskEntity> findByInstanceIdAndStepId(String instanceId, String stepId);

    List<ApprovalTaskEntity> findByInstanceId(String instanceId);
}
