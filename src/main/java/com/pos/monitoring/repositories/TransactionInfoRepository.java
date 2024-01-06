package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.TransactionInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface TransactionInfoRepository extends SoftDeleteJpaRepository<TransactionInfo> {
    @Query(value = "select ti.mfo as mfo, sum(ti.amount) as amount, sum(ti.total) as total  from transaction_infos ti  where ti.create_date between   ?1  and ?2  group by ti.mfo having ti.mfo in( ?3 )", nativeQuery = true)
    List<Map<String,Object>> getAllByTransactionFromToDate(Date fromDate, Date toDate, List<String> mfos);
    int countAllByTodayAndMfoIn(String today, List<String> mfos);

    @Query(value = "select sum(m.transaction_count) from machines m where cast(m.transaction_date as varchar) = ?1 and m.branch_mfo in ( ?2 )", nativeQuery = true)
    Optional<Integer> sumAllCountByTodayAndMfoIn(String today, List<String> mfos);

    @Query(value = "select sum(m.transaction_debit) from machines m where cast(m.transaction_date as varchar) = ?1 and m.branch_mfo in ( ?2 )", nativeQuery = true)
    Optional<Double> sumAllAmountByTodayAndMfoIn(String today, List<String> mfos);

    @Query(value = "select count(*) from machines m where synced_transaction and m.mcc in ('6010','6012','6050') and m.branch_mfo in (?1);",nativeQuery = true)
    Optional<Integer>countAllMcc(List<String> mfos);

    @Query(value = """
            select terminal_id || merchant_id as terminal_merchant, max(amount) as amount
            from transaction_infos
            where create_date > current_timestamp - interval '1 month'
            group by terminal_id, merchant_id;
            """,nativeQuery = true)
    List<Map<String,Object>>getAllMax();



}
