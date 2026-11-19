package com.company.ccm.organization.application;

import com.company.ccm.organization.domain.RoleCodes;
import com.company.ccm.organization.domain.RoleEntity;
import com.company.ccm.organization.domain.UserEntity;
import com.company.ccm.organization.infrastructure.RoleRepository;
import com.company.ccm.organization.infrastructure.UserRepository;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD_ENV = "CCM_ADMIN_PASSWORD";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsername(ADMIN_USERNAME)) {
            return;
        }
        RoleEntity adminRole = roleRepository
                .findByCode(RoleCodes.SYSTEM_ADMIN)
                .orElseThrow(() -> new IllegalStateException("SYSTEM_ADMIN role missing; run Flyway migrations"));
        String password = System.getenv().getOrDefault(ADMIN_PASSWORD_ENV, "Admin@123");
        UserEntity admin = new UserEntity();
        admin.setId("22222222-2222-2222-2222-222222222001");
        admin.setUsername(ADMIN_USERNAME);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setDisplayName("System Admin");
        admin.setEmail("admin@example.com");
        admin.setStatus("ACTIVE");
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);
        log.info("Seeded default admin user '{}'", ADMIN_USERNAME);
    }
}
