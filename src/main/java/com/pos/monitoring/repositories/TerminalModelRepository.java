package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.TerminalModel;

public interface TerminalModelRepository extends SoftDeleteJpaRepository<TerminalModel> {
    TerminalModel findByPrefixAndDeleted(String name, boolean deleted);
}
