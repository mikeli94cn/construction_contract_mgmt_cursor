package com.company.ccm.organization.application;

import com.company.ccm.organization.api.dto.CreateUserRequest;
import com.company.ccm.organization.api.dto.UpdateUserRequest;
import com.company.ccm.organization.api.dto.UserResponse;
import com.company.ccm.organization.domain.RoleEntity;
import com.company.ccm.organization.domain.UserEntity;
import com.company.ccm.organization.infrastructure.RoleRepository;
import com.company.ccm.organization.infrastructure.UserRepository;
import com.company.ccm.shared.domain.BusinessException;
import com.company.ccm.shared.domain.NotFoundException;
import com.company.ccm.shared.security.SecurityUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserResponse me() {
        return toResponse(requireUser(SecurityUtils.currentUserId()));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(String id) {
        return toResponse(requireUser(id));
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username already exists");
        }
        UserEntity user = new UserEntity();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setStatus("ACTIVE");
        user.setRoles(resolveRoles(request.roleCodes()));
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        UserEntity user = requireUser(id);
        if (request.displayName() != null) {
            user.setDisplayName(request.displayName());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.status() != null) {
            user.setStatus(request.status());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse assignRoles(String id, List<String> roleCodes) {
        UserEntity user = requireUser(id);
        user.setRoles(resolveRoles(roleCodes));
        return toResponse(userRepository.save(user));
    }

    private Set<RoleEntity> resolveRoles(List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            throw new BusinessException("At least one role is required");
        }
        Set<RoleEntity> roles = new HashSet<>();
        for (String code : roleCodes) {
            RoleEntity role = roleRepository
                    .findByCode(code)
                    .orElseThrow(() -> new NotFoundException("Role not found: " + code));
            roles.add(role);
        }
        return roles;
    }

    private UserEntity requireUser(String id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private UserResponse toResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                user.getRoles().stream().map(RoleEntity::getCode).sorted().toList(),
                user.getVersion());
    }
}
