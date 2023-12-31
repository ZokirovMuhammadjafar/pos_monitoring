package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.TransactionInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public interface TransactionInfoRepository extends SoftDeleteJpaRepository<TransactionInfo> {
    @Query(value = "select ti.mfo as mfo, sum(ti.amount) as amount, sum(ti.total) as total  from TransactionInfo ti  where ti.transactionsDay between   ?1  and ?2  group by ti.mfo having ti.mfo in( ?3 )")
    List<Map<String, Object>> getAllByTransactionFromToDate(LocalDate fromDate, LocalDate toDate, List<String> mfos);
    @Query(value = "select sum(m.total) as transaction,ceiling(sum(m.amount)/100000000) as transaction_sum,count(m) as onCount from TransactionInfo m where m.mfo in ( ?1 ) and m.transactionsDay = ?2 ")
    Map<String, Long> sumAllStatAndMfoIn(List<String> mfos, LocalDate yesterday);

    @Query(value = "select count(*) from machines m where synced_transaction and m.mcc in ('6010','6012','6050') and m.branch_mfo in (?1);", nativeQuery = true)
    Optional<Integer> countAllMcc(List<String> mfos);

    @Query(value = """
            select terminal_id || merchant_id as terminal_merchant, max(amount) as amount
            from transaction_infos
            where create_date > current_timestamp - interval '1 month'
            group by terminal_id, merchant_id;
            """, nativeQuery = true)
    List<Map<String, Object>> getAllMax();


}
