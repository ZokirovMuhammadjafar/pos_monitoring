package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.TransactionInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionInfoRepository extends SoftDeleteJpaRepository<TransactionInfo> {
}
