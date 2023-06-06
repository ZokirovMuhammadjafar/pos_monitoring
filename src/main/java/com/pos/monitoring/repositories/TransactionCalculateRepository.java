package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.TransactionCalculate;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCalculateRepository extends SoftDeleteJpaRepository<TransactionCalculate> {
}
