package com.pos.monitoring.services.impl;

import com.pos.monitoring.entities.MachineHistoryState;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.MachineHistory;
import com.pos.monitoring.repositories.MachineHistoryRepository;
import com.pos.monitoring.services.MachineHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MachineHistoryServiceImpl implements MachineHistoryService {
    private final MachineHistoryRepository machineHistoryRepository;

    @Override
    public MachineHistory createChangeInst(Machine oldMachine, Machine machine) {
        MachineHistory machineHistory = new MachineHistory();
        machineHistory.setFromInstId(oldMachine.getInstId());
        machineHistory.setToInstId(machine.getInstId());
        machineHistory.setSrNumber(machine.getSrNumber());
        machineHistory.setState(MachineHistoryState.CHANGE_INS);
        return machineHistoryRepository.save(machineHistory);
    }

    @Override
    public MachineHistory createChangeMfo(Machine oldMachine, Machine machine) {
        MachineHistory machineHistory = new MachineHistory();
        machineHistory.setFromMfo(oldMachine.getBranchMfo());
        machineHistory.setToMfo(machine.getBranchMfo());
        machineHistory.setSrNumber(machine.getSrNumber());
        machineHistory.setState(MachineHistoryState.CHANGE_MFO);
        return machineHistoryRepository.save(machineHistory);
    }
}
