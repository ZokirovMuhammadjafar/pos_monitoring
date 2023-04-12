package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.TerminalModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Repository
public interface MachineRepository extends SoftDeleteJpaRepository<Machine> {


    Machine findBySrNumberAndDeleted(String srNumber, boolean deleted);

    @Modifying
    @Query("update Machine m set m.deleted = true where m.prefix = ?1 ")
    void deleteByPrefix(String prefix);

    @Query("from Machine m where m.deleted=false and m.prefix=?1")
    Stream<Machine> findPrefix(String prefix);


    @Query(value = "select m.state as state,count(m.state) as number from machines m where m.inst_id= ?1 group by m.state",nativeQuery = true)
    List<Map<String,Object>> getStat(String instId);
}
