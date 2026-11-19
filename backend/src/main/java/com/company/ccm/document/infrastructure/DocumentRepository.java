package com.company.ccm.document.infrastructure;

import com.company.ccm.document.domain.DocumentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<DocumentEntity, String> {
    List<DocumentEntity> findByOwnerTypeAndOwnerId(String ownerType, String ownerId);
}
