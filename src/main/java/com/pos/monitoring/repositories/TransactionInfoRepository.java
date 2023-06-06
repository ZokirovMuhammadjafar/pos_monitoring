package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.TransactionInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionInfoRepository extends SoftDeleteJpaRepository<TransactionInfo> {

    @Query(value = "select ti.mfo, sum(ti.amount), sum(ti.total) " +
            " from transaction_infos ti where ti.create_date >= ?1 group by ti.mfo;", nativeQuery = true)
    List<Object[]> getAllByTransactionDate(Date date);
}
