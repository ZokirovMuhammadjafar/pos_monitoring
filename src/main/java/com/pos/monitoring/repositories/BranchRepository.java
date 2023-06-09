package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.Branch;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface BranchRepository extends SoftDeleteJpaRepository<Branch> {

    Branch findByMfoAndDeletedFalse(String mfo);

    @Query("select b from Branch b where b.mfo in (select t.instId from Machine t group by t.instId)")
    List<Branch> findIntsId();

    List<Branch> findByParentAndDeletedFalse(Branch parent);
}
