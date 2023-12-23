package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.Branch;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Repository
public interface BranchRepository extends SoftDeleteJpaRepository<Branch> {

    Branch findByMfoAndDeletedFalse(String mfo);

    Set<Branch> findByMfoInAndDeletedFalse(List<String> mfo);

    @Query("select b from Branch  b join b.parent as p where b.deleted=false and p.mfo = ?1 ")
    Set<Branch> findAllParentAndDeletedFalse(String mfo);

    @Query("select b from Branch b where b.mfo in (select t.instId from Machine t group by t.instId)")
    List<Branch> findIntsId();

    @Query("select  b from Branch  b where b.parent in ( ?1 )")
    Set<Branch> findByBranchesDeletedFalse(Set<Branch> parent);
}
