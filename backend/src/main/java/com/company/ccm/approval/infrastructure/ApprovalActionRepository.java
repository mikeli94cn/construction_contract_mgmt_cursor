package com.company.ccm.approval.infrastructure;

import com.company.ccm.approval.domain.ApprovalActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalActionRepository extends JpaRepository<ApprovalActionEntity, String> {}
