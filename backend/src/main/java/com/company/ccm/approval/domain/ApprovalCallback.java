package com.company.ccm.approval.domain;

public interface ApprovalCallback {
    ApprovalTargetType supports();

    void onApproved(String targetId);

    void onRejected(String targetId);
}
