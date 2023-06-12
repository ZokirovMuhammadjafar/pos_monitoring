package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.enums.MachineState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.pos.monitoring.repositories.system.queries.ConstantQueries.GET_TABLE_BY_INST;
import static com.pos.monitoring.repositories.system.queries.ConstantQueries.GET_TABLE_BY_MFOS;

@Repository
public interface MachineRepository extends SoftDeleteJpaRepository<Machine> {


    Machine findBySrNumberAndDeleted(String srNumber, boolean deleted);

    @Modifying
    @Query("update Machine m set m.deleted = true where m.prefix = ?1 ")
    void deleteByPrefix(String prefix);

    @Query("from Machine m where m.deleted=false and m.prefix=?1")
    Stream<Machine> findPrefix(String prefix);


    @Query(value = "select m.state as state,count(m.state) as number from machines m where m.inst_id= ?1 group by m.state", nativeQuery = true)
    List<Map<String, Object>> getStat(String instId);

    @Query(value = GET_TABLE_BY_INST, nativeQuery = true)
    List<Map<String, String>> getByInstId(String instId);

    @Query(value = "select m.state as state, count(m.state) as number from machines m where m.inst_id= ?1 group by m.state", nativeQuery = true)
    List<Map<String, Object>> getState(String instId);

    @Query(value = "select m.state as state, count(m.state) as number from machines m where m.branch_mfo in (?1) group by m.state", nativeQuery = true)
    List<Map<String, Object>> getStatisticByMfos(List<String> mfos);

    List<Machine> findAllByStateOrderByIdAsc(MachineState state, Pageable pageable);

    int countAllByState(MachineState state);

    @Query("select count(m) from Machine m where m.instId = ?1 and (m.state=0 or m.state=3)")
    Long getAllWorkingTerminal(String instId);

    @Query(value = GET_TABLE_BY_MFOS,nativeQuery = true)
    List<Map<String, String>> getbyMfoList(List<String> mfo);
}
