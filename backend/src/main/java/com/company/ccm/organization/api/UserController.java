package com.company.ccm.organization.api;

import com.company.ccm.organization.api.dto.AssignRolesRequest;
import com.company.ccm.organization.api.dto.CreateUserRequest;
import com.company.ccm.organization.api.dto.UpdateUserRequest;
import com.company.ccm.organization.api.dto.UserResponse;
import com.company.ccm.organization.application.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse me() {
        return userService.me();
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public List<UserResponse> list() {
        return userService.listUsers();
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public UserResponse get(@PathVariable String id) {
        return userService.getUser(id);
    }

    @PatchMapping("/users/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public UserResponse update(@PathVariable String id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @PostMapping("/users/{id}/roles")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public UserResponse assignRoles(@PathVariable String id, @Valid @RequestBody AssignRolesRequest request) {
        return userService.assignRoles(id, request.roleCodes());
    }
}
