package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends SoftDeleteJpaRepository<Transaction> {
}
