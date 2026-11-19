package com.company.ccm.changeorder.application;

import com.company.ccm.approval.domain.ApprovalCallback;
import com.company.ccm.approval.domain.ApprovalTargetType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ChangeOrderApprovalCallback implements ApprovalCallback {

    private final ChangeOrderService changeOrderService;

    public ChangeOrderApprovalCallback(@Lazy ChangeOrderService changeOrderService) {
        this.changeOrderService = changeOrderService;
    }

    @Override
    public ApprovalTargetType supports() {
        return ApprovalTargetType.CHANGE_ORDER;
    }

    @Override
    public void onApproved(String targetId) {
        changeOrderService.markApproved(targetId);
    }

    @Override
    public void onRejected(String targetId) {
        changeOrderService.markRejected(targetId);
    }
}
