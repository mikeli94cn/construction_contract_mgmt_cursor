package com.company.ccm.contract.api;

import com.company.ccm.contract.api.dto.ContractDtos.ContractResponse;
import com.company.ccm.contract.api.dto.ContractDtos.CreateContractRequest;
import com.company.ccm.contract.api.dto.ContractDtos.UpdateContractRequest;
import com.company.ccm.contract.application.ContractService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping("/contracts")
    public List<ContractResponse> listAll() {
        return contractService.listAll();
    }

    @GetMapping("/projects/{projectId}/contracts")
    public List<ContractResponse> listByProject(@PathVariable String projectId) {
        return contractService.listByProject(projectId);
    }

    @PostMapping("/projects/{projectId}/contracts")
    public ContractResponse create(
            @PathVariable String projectId, @Valid @RequestBody CreateContractRequest request) {
        return contractService.create(projectId, request);
    }

    @GetMapping("/contracts/{id}")
    public ContractResponse get(@PathVariable String id) {
        return contractService.get(id);
    }

    @PatchMapping("/contracts/{id}")
    public ContractResponse update(@PathVariable String id, @Valid @RequestBody UpdateContractRequest request) {
        return contractService.update(id, request);
    }

    @PostMapping("/contracts/{id}/submit")
    public ContractResponse submit(@PathVariable String id) {
        return contractService.submit(id);
    }

    @PostMapping("/contracts/{id}/activate")
    public ContractResponse activate(@PathVariable String id) {
        return contractService.activate(id);
    }
}
