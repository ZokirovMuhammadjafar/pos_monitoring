package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.TransactionCalculate;
import com.pos.monitoring.entities.TransactionInfo;
import com.pos.monitoring.entities.enums.CalculateType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface TransactionInfoRepository extends SoftDeleteJpaRepository<TransactionInfo> {

    @Query(value = "select ti.mfo, sum(ti.amount), sum(ti.total) from transaction_infos ti where ti.create_date >= ?1 group by ti.mfo;", nativeQuery = true)
    List<Object[]> getAllByTransactionDate(Date date);
    @Query(value = "select ti.mfo as mfo, sum(ti.amount) as amount, sum(ti.total) as total  from transaction_infos ti  where ti.create_date between   ?1  and ?2  group by ti.mfo having ti.mfo in( ?3 )", nativeQuery = true)
    List<Map<String,Object>> getAllByTransactionFromToDate(Date fromDate, Date toDate, List<String> mfos);
    int countAllByTodayAndMfoIn(String today, List<String> mfos);

    @Query(value = "select sum(m.transaction_count) from machines m where cast(m.transaction_date as varchar) = ?1 and m.branch_mfo in ( ?2 )", nativeQuery = true)
    Optional<Integer> sumAllCountByTodayAndMfoIn(String today, List<String> mfos);

    @Query(value = "select sum(m.transaction_debit) from machines m where cast(m.transaction_date as varchar) = ?1 and m.branch_mfo in ( ?2 )", nativeQuery = true)
    Optional<Double> sumAllAmountByTodayAndMfoIn(String today, List<String> mfos);

    @Query(value = "select count(*) from machines m where synced_transaction and m.mcc in ('6010','6012','6050') and m.branch_mfo in (?1);",nativeQuery = true)
    Optional<Integer>countAllMcc(List<String> mfos);




}
