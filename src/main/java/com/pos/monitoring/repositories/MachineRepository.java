package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.enums.MachineState;
import com.pos.monitoring.entities.enums.SynchronizeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.pos.monitoring.repositories.system.queries.ConstantQueries.GET_TABLE_BY_MFOS;
import static com.pos.monitoring.repositories.system.queries.ConstantQueries.REPORT_QUERY_POS_MONITORING;

@Repository
public interface MachineRepository extends SoftDeleteJpaRepository<Machine> {


    Machine findBySrNumberAndDeleted(String srNumber, boolean deleted);

    @Modifying
    @Query("update Machine m set m.deleted = true where m.prefix = ?1 ")
    void deleteByPrefix(String prefix);

    @Query(value = "select m.state as state, count(m.state) as number from machines m where m.branch_mfo in (?1) group by m.state", nativeQuery = true)
    List<Map<String, Object>> getStatisticByMfos(List<String> mfos);

    List<Machine> findAllByStateOrStateOrderByIdAsc(MachineState state, MachineState state2, Pageable pageable);

    @Query(value = """
            select *
            from machines
            where state in (0, 3)
              and daily_transaction_level > -1
            order by daily_transaction_level desc
            LIMIT ?1 OFFSET ?2 ;
            """, nativeQuery = true)
    List<Machine> getAllTerminalsByTransactionLevel(Integer limit, Integer offset);


    @Query(value = """
            select *
            from machines
            where state in (0, 3)
              and daily_transaction_level = -1
            order by id desc
            LIMIT ?1 OFFSET ?2 ;
            """, nativeQuery = true)
    List<Machine> findAllByDailyTransactionLevel(Integer limit, Integer offset);

    @Query(value = "select count(*) from machines m where m.state in (0,3) and m.daily_transaction_level >-1", nativeQuery = true)
    int countAllTerminalsWithoutKassa();

    @Query(value = GET_TABLE_BY_MFOS, nativeQuery = true)
    List<Map<String, String>> getbyMfoList(List<String> mfo);

    @Query(value = "select count(*) from machines m where m.state in (0,3) and m.daily_transaction_level =-1", nativeQuery = true)
    int countAllTerminalsWithKassa();

    @Query(value = REPORT_QUERY_POS_MONITORING, nativeQuery = true)
    List<Map<String, Object>> report(String mfo);
}
