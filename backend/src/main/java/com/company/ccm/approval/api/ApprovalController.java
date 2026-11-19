package com.company.ccm.approval.api;

import com.company.ccm.approval.api.dto.ApprovalDtos.ApprovalActionRequest;
import com.company.ccm.approval.api.dto.ApprovalDtos.ApprovalDefinitionRequest;
import com.company.ccm.approval.api.dto.ApprovalDtos.ApprovalDefinitionResponse;
import com.company.ccm.approval.api.dto.ApprovalDtos.ApprovalTaskResponse;
import com.company.ccm.approval.application.ApprovalService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("/approval-definitions")
    public List<ApprovalDefinitionResponse> listDefinitions() {
        return approvalService.listDefinitions();
    }

    @PostMapping("/approval-definitions")
    public ApprovalDefinitionResponse createDefinition(@Valid @RequestBody ApprovalDefinitionRequest request) {
        return approvalService.createDefinition(request);
    }

    @PutMapping("/approval-definitions/{id}")
    public ApprovalDefinitionResponse updateDefinition(
            @PathVariable String id, @Valid @RequestBody ApprovalDefinitionRequest request) {
        return approvalService.updateDefinition(id, request);
    }

    @GetMapping("/approval-tasks/mine")
    public List<ApprovalTaskResponse> myTasks() {
        return approvalService.myTasks();
    }

    @PostMapping("/approval-tasks/{id}/approve")
    public void approve(@PathVariable String id, @RequestBody(required = false) ApprovalActionRequest request) {
        approvalService.approve(id, request);
    }

    @PostMapping("/approval-tasks/{id}/reject")
    public void reject(@PathVariable String id, @RequestBody(required = false) ApprovalActionRequest request) {
        approvalService.reject(id, request);
    }
}
