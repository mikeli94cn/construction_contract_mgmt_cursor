package com.company.ccm.payment.infrastructure;

import com.company.ccm.payment.domain.PaymentApplicationEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentApplicationRepository extends JpaRepository<PaymentApplicationEntity, String> {
    List<PaymentApplicationEntity> findByContractId(String contractId);

    boolean existsByPaymentNo(String paymentNo);

    @Query(
            """
            select coalesce(sum(p.amount), 0) from PaymentApplicationEntity p
            where p.contractId = :contractId and p.status in ('APPROVED', 'PAID', 'PENDING_APPROVAL')
            """)
    BigDecimal sumReservedOrPaid(String contractId);

    @Query(
            """
            select coalesce(sum(p.amount), 0) from PaymentApplicationEntity p
            where p.contractId = :contractId and p.status in ('APPROVED', 'PAID')
            """)
    BigDecimal sumApprovedOrPaid(String contractId);
}
