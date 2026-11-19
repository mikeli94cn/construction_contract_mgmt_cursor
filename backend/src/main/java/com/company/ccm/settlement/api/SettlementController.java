package com.company.ccm.settlement.api;

import com.company.ccm.settlement.api.dto.SettlementDtos.CreateSettlementRequest;
import com.company.ccm.settlement.api.dto.SettlementDtos.SettlementResponse;
import com.company.ccm.settlement.application.SettlementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping("/contracts/{contractId}/settlements")
    public SettlementResponse getByContract(@PathVariable String contractId) {
        return settlementService.getByContract(contractId);
    }

    @PostMapping("/contracts/{contractId}/settlements")
    public SettlementResponse create(
            @PathVariable String contractId, @Valid @RequestBody CreateSettlementRequest request) {
        return settlementService.create(contractId, request);
    }

    @PostMapping("/settlements/{id}/submit")
    public SettlementResponse submit(@PathVariable String id) {
        return settlementService.submit(id);
    }

    @PostMapping("/settlements/{id}/seal")
    public SettlementResponse seal(@PathVariable String id) {
        return settlementService.seal(id);
    }
}
