package com.pos.monitoring.services;

import com.pos.monitoring.dtos.pageable.MachineHistoryPageableSearch;
import com.pos.monitoring.dtos.pageable.TerminalModelPageableSearch;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.MachineHistory;
import com.pos.monitoring.entities.TerminalModel;
import org.springframework.data.domain.Page;

public interface MachineHistoryService {

    void createChangeInst(Machine oldMachine, Machine machine);

    MachineHistory createChangeMfo(Machine oldMachine, Machine machine);

    Page<MachineHistory> getAll(MachineHistoryPageableSearch pageableSearch);

    MachineHistory get(Long id);
}
