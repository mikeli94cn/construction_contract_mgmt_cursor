package com.company.ccm.organization.infrastructure;

import com.company.ccm.organization.domain.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("select u from UserEntity u join u.roles r where r.code = :roleCode and u.status = 'ACTIVE'")
    List<UserEntity> findActiveByRoleCode(String roleCode);
}
