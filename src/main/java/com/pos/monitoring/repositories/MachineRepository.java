package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.Machine;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends SoftDeleteJpaRepository<Machine> {


    Machine findBySrNumber(String srNumber);
}
