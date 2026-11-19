package com.company.ccm.contract.infrastructure;

import com.company.ccm.contract.domain.ContractEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<ContractEntity, String> {
    List<ContractEntity> findByProjectId(String projectId);

    boolean existsByContractNo(String contractNo);
}
