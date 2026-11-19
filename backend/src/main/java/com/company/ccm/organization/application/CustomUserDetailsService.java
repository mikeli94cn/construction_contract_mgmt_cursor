package com.company.ccm.organization.application;

import com.company.ccm.organization.domain.RoleEntity;
import com.company.ccm.organization.domain.UserEntity;
import com.company.ccm.organization.infrastructure.UserRepository;
import com.company.ccm.shared.security.UserPrincipal;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<String> roles = user.getRoles().stream().map(RoleEntity::getCode).toList();
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                "ACTIVE".equals(user.getStatus()),
                roles);
    }
}
