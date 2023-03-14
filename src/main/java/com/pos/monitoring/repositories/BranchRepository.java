package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.Branch;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends SoftDeleteJpaRepository<Branch> {

    Branch findByMfoAndDeleted(String mfo, boolean deleted);
}
