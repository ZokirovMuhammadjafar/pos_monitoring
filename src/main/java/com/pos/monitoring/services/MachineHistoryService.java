package com.pos.monitoring.services;

import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.MachineHistory;

public interface MachineHistoryService {

    MachineHistory createChangeInst(Machine oldMachine, Machine machine);

    MachineHistory createChangeMfo(Machine oldMachine, Machine machine);
}
