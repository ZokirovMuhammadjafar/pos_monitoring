package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.Machine;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends SoftDeleteJpaRepository<Machine> {


    Machine findBySrNumberAndDeleted(String srNumber, boolean deleted);

    @Query("update Machine m set m.deleted = true where m.prefix = ?1 ")
    void deleteByPrefix(String prefix);


}
