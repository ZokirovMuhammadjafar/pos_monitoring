package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.DailySynchronize;
import com.pos.monitoring.entities.enums.SynchronizeType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailySynchronizeRepository extends SoftDeleteJpaRepository<DailySynchronize> {

    Optional<DailySynchronize> findByTodayAndSynchronizationType(String today, SynchronizeType synchronizationType);
}
