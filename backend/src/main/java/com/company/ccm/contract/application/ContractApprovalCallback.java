package com.company.ccm.contract.application;

import com.company.ccm.approval.domain.ApprovalCallback;
import com.company.ccm.approval.domain.ApprovalTargetType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ContractApprovalCallback implements ApprovalCallback {

    private final ContractService contractService;

    public ContractApprovalCallback(@Lazy ContractService contractService) {
        this.contractService = contractService;
    }

    @Override
    public ApprovalTargetType supports() {
        return ApprovalTargetType.CONTRACT;
    }

    @Override
    public void onApproved(String targetId) {
        contractService.markApproved(targetId);
    }

    @Override
    public void onRejected(String targetId) {
        contractService.markRejected(targetId);
    }
}
