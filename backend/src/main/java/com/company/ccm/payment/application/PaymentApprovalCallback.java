package com.company.ccm.payment.application;

import com.company.ccm.approval.domain.ApprovalCallback;
import com.company.ccm.approval.domain.ApprovalTargetType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class PaymentApprovalCallback implements ApprovalCallback {

    private final PaymentService paymentService;

    public PaymentApprovalCallback(@Lazy PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public ApprovalTargetType supports() {
        return ApprovalTargetType.PAYMENT;
    }

    @Override
    public void onApproved(String targetId) {
        paymentService.markApproved(targetId);
    }

    @Override
    public void onRejected(String targetId) {
        paymentService.markRejected(targetId);
    }
}
