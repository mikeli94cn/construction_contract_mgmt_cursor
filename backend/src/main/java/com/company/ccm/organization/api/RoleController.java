package com.company.ccm.organization.api;

import com.company.ccm.organization.api.dto.RoleResponse;
import com.company.ccm.organization.infrastructure.RoleRepository;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public List<RoleResponse> list() {
        return roleRepository.findAll().stream()
                .map(r -> new RoleResponse(r.getId(), r.getCode(), r.getName()))
                .toList();
    }
}
