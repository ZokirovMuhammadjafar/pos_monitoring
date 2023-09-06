package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.enums.MachineState;
import com.pos.monitoring.entities.enums.SynchronizeType;
import com.pos.monitoring.repositories.system.specifications.MachineSpecification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.pos.monitoring.repositories.system.specifications.ConstantQueries.GET_TABLE_BY_MFOS;
import static com.pos.monitoring.repositories.system.specifications.ConstantQueries.REPORT_QUERY_POS_MONITORING;

@Repository
public interface MachineRepository extends SoftDeleteJpaRepository<Machine> {

    Machine findBySrNumberAndDeleted(String srNumber, boolean deleted);

    @Query(value = "select m.state as state, count(m.state) as number from machines m where m.branch_mfo in (?1) group by m.state", nativeQuery = true)
    List<Map<String, Object>> getStatisticByMfos(List<String> mfos);

    default List<Machine> getAllMachineForTransactionRequest(SynchronizeType synchronizeType,Pageable pageable) {
      return findAll(MachineSpecification.machinaSyncType(synchronizeType).and(MachineSpecification.machineStatusIn(List.of(MachineState.HAS_CONTRACT_WITH_7003,MachineState.HAS_NOT_CONTRACT_WORKING_7003))).and(MachineSpecification.machineOrderBy("id"))
       ,pageable).stream().toList();
    }
    @Query(value = GET_TABLE_BY_MFOS, nativeQuery = true)
    List<Map<String, String>> getbyMfoList(List<String> mfo);

    @Query(value = REPORT_QUERY_POS_MONITORING, nativeQuery = true)
    List<Map<String, Object>> report(String mfo);

    @Modifying
    @Transactional
    @Query(value = "update Machine m set m.transactionCount=0 ,m.transactionDate=null , m.transactionDebit=0 , m.syncedTransaction=false where 1=1")
    void synchTransactionFalse();
}
