package com.company.ccm.changeorder.api;

import com.company.ccm.changeorder.api.dto.ChangeOrderDtos.ChangeOrderResponse;
import com.company.ccm.changeorder.api.dto.ChangeOrderDtos.CreateChangeOrderRequest;
import com.company.ccm.changeorder.api.dto.ChangeOrderDtos.UpdateChangeOrderRequest;
import com.company.ccm.changeorder.application.ChangeOrderService;
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
public class ChangeOrderController {

    private final ChangeOrderService changeOrderService;

    public ChangeOrderController(ChangeOrderService changeOrderService) {
        this.changeOrderService = changeOrderService;
    }

    @GetMapping("/contracts/{contractId}/change-orders")
    public List<ChangeOrderResponse> list(@PathVariable String contractId) {
        return changeOrderService.listByContract(contractId);
    }

    @PostMapping("/contracts/{contractId}/change-orders")
    public ChangeOrderResponse create(
            @PathVariable String contractId, @Valid @RequestBody CreateChangeOrderRequest request) {
        return changeOrderService.create(contractId, request);
    }

    @GetMapping("/change-orders/{id}")
    public ChangeOrderResponse get(@PathVariable String id) {
        return changeOrderService.get(id);
    }

    @PatchMapping("/change-orders/{id}")
    public ChangeOrderResponse update(
            @PathVariable String id, @Valid @RequestBody UpdateChangeOrderRequest request) {
        return changeOrderService.update(id, request);
    }

    @PostMapping("/change-orders/{id}/submit")
    public ChangeOrderResponse submit(@PathVariable String id) {
        return changeOrderService.submit(id);
    }
}
