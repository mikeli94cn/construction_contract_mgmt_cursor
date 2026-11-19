package com.company.ccm.organization.infrastructure;

import com.company.ccm.organization.domain.RefreshTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);
}
