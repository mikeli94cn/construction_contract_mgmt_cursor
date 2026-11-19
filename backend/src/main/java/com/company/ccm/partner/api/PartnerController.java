package com.company.ccm.partner.api;

import com.company.ccm.partner.api.dto.PartnerDtos.CreatePartnerRequest;
import com.company.ccm.partner.api.dto.PartnerDtos.PartnerResponse;
import com.company.ccm.partner.api.dto.PartnerDtos.UpdatePartnerRequest;
import com.company.ccm.partner.application.PartnerService;
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
@RequestMapping("/api/v1/partners")
public class PartnerController {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @GetMapping
    public List<PartnerResponse> list() {
        return partnerService.list();
    }

    @PostMapping
    public PartnerResponse create(@Valid @RequestBody CreatePartnerRequest request) {
        return partnerService.create(request);
    }

    @GetMapping("/{id}")
    public PartnerResponse get(@PathVariable String id) {
        return partnerService.get(id);
    }

    @PatchMapping("/{id}")
    public PartnerResponse update(@PathVariable String id, @Valid @RequestBody UpdatePartnerRequest request) {
        return partnerService.update(id, request);
    }
}
