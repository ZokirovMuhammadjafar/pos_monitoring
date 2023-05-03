package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.Branch;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends SoftDeleteJpaRepository<Branch> {

    Branch findByMfoAndDeletedFalse(String mfo);

    List<Branch> findByParentAndDeletedFalse(Branch parent);
}
