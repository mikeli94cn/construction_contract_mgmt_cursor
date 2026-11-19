package com.company.ccm.settlement.application;

import com.company.ccm.approval.domain.ApprovalCallback;
import com.company.ccm.approval.domain.ApprovalTargetType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class SettlementApprovalCallback implements ApprovalCallback {

    private final SettlementService settlementService;

    public SettlementApprovalCallback(@Lazy SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @Override
    public ApprovalTargetType supports() {
        return ApprovalTargetType.SETTLEMENT;
    }

    @Override
    public void onApproved(String targetId) {
        settlementService.markApproved(targetId);
    }

    @Override
    public void onRejected(String targetId) {
        settlementService.markRejected(targetId);
    }
}
