package com.company.ccm.payment.api;

import com.company.ccm.payment.api.dto.PaymentDtos.CreatePaymentRequest;
import com.company.ccm.payment.api.dto.PaymentDtos.PaymentResponse;
import com.company.ccm.payment.api.dto.PaymentDtos.UpdatePaymentRequest;
import com.company.ccm.payment.application.PaymentService;
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
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/contracts/{contractId}/payments")
    public List<PaymentResponse> list(@PathVariable String contractId) {
        return paymentService.listByContract(contractId);
    }

    @PostMapping("/contracts/{contractId}/payments")
    public PaymentResponse create(
            @PathVariable String contractId, @Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.create(contractId, request);
    }

    @GetMapping("/payments/{id}")
    public PaymentResponse get(@PathVariable String id) {
        return paymentService.get(id);
    }

    @PatchMapping("/payments/{id}")
    public PaymentResponse update(@PathVariable String id, @Valid @RequestBody UpdatePaymentRequest request) {
        return paymentService.update(id, request);
    }

    @PostMapping("/payments/{id}/submit")
    public PaymentResponse submit(@PathVariable String id) {
        return paymentService.submit(id);
    }
}
