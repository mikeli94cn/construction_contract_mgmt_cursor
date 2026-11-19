package com.company.ccm.changeorder.infrastructure;

import com.company.ccm.changeorder.domain.ChangeOrderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeOrderRepository extends JpaRepository<ChangeOrderEntity, String> {
    List<ChangeOrderEntity> findByContractId(String contractId);

    boolean existsByChangeNo(String changeNo);
}
