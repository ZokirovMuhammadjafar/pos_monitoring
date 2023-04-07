package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.TerminalModel;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TerminalModelRepository extends SoftDeleteJpaRepository<TerminalModel> {
    TerminalModel findByPrefixAndDeleted(String name, boolean deleted);

    @Modifying
    @Query(value = "update TerminalModel  set deleted = true , updateDate = current_timestamp  where id =  :id ")
    void deleteTerminal(@Param(value = "id") Long id);


    List<TerminalModel> findAllByDeletedFalse(Pageable pageable);
}
