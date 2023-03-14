package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.enums.MachineState;
import com.pos.monitoring.entities.Branch;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.MachineHistory;
import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.repositories.BranchRepository;
import com.pos.monitoring.repositories.MachineRepository;
import com.pos.monitoring.repositories.TerminalModelRepository;
import com.pos.monitoring.repositories.system.Connection8005;
import com.pos.monitoring.services.MachineHistoryService;
import com.pos.monitoring.services.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private final Connection8005 connection8005;

    private final MachineRepository machineRepository;
    private final BranchRepository branchRepository;
    private final MachineHistoryService machineHistoryService;
    private final TerminalModelRepository terminalModelRepository;

    @Override
    public void synchronize() {
        List<Machine> allChangeMachines = connection8005.getAllChangeMachines();
        for (Machine machine : allChangeMachines) {
            String branchMfo = machine.getBranchMfo();
            Branch branch = branchRepository.findByMfoAndDeleted(branchMfo, false);
            if (branch == null) {
                // TODO: 3/13/2023 shuni telegram bot orqali jonatib qoyish kerak mfo tid mid
                continue;
            }
            machine.setBranch(branch);
            if (machine.getSrNumber() == null && machine.getSrNumber().length() > 2) {
                // TODO: 3/13/2023 shuni telegramga tashlab qoyish kerak branch va tid mid sr
                continue;
            }
            machine.setPrefix(machine.getSrNumber().substring(0, 3));
            stateChoose(machine);
        }
    }

    /**
     * this function change state
     * @param machine finally machine will be saved with change
     */
    private void stateChoose(Machine machine) {
        TerminalModel validPrefix = terminalModelRepository.findByPrefixAndDeleted(machine.getPrefix(), true);
        if (validPrefix.getValid()) {
            if (machine.getTerminalId() == null && machine.getMerchantId() == null) {
                machine.setState(MachineState.HAS_WAREHOUSE);
            } else if (machine.getTerminalId() == null || machine.getMerchantId() == null) {
                machine.setState(MachineState.HAS_ERROR);
            } else {
                Machine oldMachine = machineRepository.findBySrNumber(machine.getSrNumber());
                if (oldMachine != null && !oldMachine.getInstId().equals(machine.getInstId())) {
                    MachineHistory newMachineHistory = machineHistoryService.createChangeInst(oldMachine, machine);
                } else {
                    if (oldMachine == null) {
                        Machine newMachine = createMachine(machine);
                    } else {
                        if (!oldMachine.getBranchMfo().equals(machine.getBranchMfo())) {
                            machineHistoryService.createChangeMfo(oldMachine,machine);
                        }
                    }
                }
            }
        } else {
            machine.setState(MachineState.HAS_NO_USED);
        }
    }

    private Machine createMachine(Machine machine) {
        machine.setState(MachineState.NEW_7003);
        return machineRepository.save(machine);

    }
}
