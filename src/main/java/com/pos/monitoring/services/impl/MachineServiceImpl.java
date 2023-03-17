package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.enums.MachineState;
import com.pos.monitoring.entities.Branch;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.repositories.BranchRepository;
import com.pos.monitoring.repositories.MachineRepository;
import com.pos.monitoring.repositories.TerminalModelRepository;
import com.pos.monitoring.repositories.system.Connection8005;
import com.pos.monitoring.services.MachineHistoryService;
import com.pos.monitoring.services.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void synchronize() {
        List<Machine> allChangeMachines = connection8005.getAllChangeMachines();
        for (Machine machine : allChangeMachines) {
            String branchMfo = machine.getBranchMfo();
            if (branchMfo != null) {
                Branch branch = branchRepository.findByMfoAndDeleted(branchMfo, false);
                if (branch == null) {
                    // TODO: 3/13/2023 shuni telegram bot orqali jonatib qoyish kerak mfo tid mid
                    continue;
                }
                machine.setBranch(branch);
            }
            if (machine.getSrNumber() != null && machine.getSrNumber().length() <= 3) {
                // TODO: 3/13/2023 shuni telegramga tashlab qoyish kerak branch va tid mid sr
                continue;
            }
            machine.setPrefix(machine.getSrNumber().substring(0, 3));
            stateChoose(machine);
        }
    }

    /**
     * this function change state
     *
     * @param machine finally machine will be saved with change
     */

    private void stateChoose(Machine machine) {
        TerminalModel validPrefix = terminalModelRepository.findByPrefixAndDeleted(machine.getPrefix(), false);
        Machine oldMachine = machineRepository.findBySrNumber(machine.getSrNumber());
        if (oldMachine == null) {
            if (validPrefix == null) {
                // TODO: 3/14/2023 telegramga log tashlash kerak
                System.out.println(machine.getPrefix());
                return;
            }
            if (machine.getIsContract()) {
                if (machine.getTerminalId() != null && machine.getMerchantId() != null && machine.getInstId() != null) {

                    if (validPrefix.getValid()) {
                        machine.setState(MachineState.HAS_CONTRACT_WITH_7003);
                    } else {
                        machine.setState(MachineState.HAS_CONTRACT_NOT_7003);
                    }
                } else if (machine.getTerminalId() == null && machine.getMerchantId() == null && machine.getInstId() != null) {
                    if (validPrefix.getValid()) {
                        machine.setState(MachineState.HAS_CONTRACT_STAY_WAREHOUSE);
                    } else {
                        machine.setState(MachineState.HAS_CONTRACT_NOT_7003);
                    }

                }
            } else {
                if (machine.getTerminalId() != null && machine.getMerchantId() != null && machine.getInstId() != null) {
                    if (validPrefix != null) {
                        if (validPrefix.getValid()) {
                            machine.setState(MachineState.HAS_NOT_CONTRACT_WORKING_7003);
                        } else {
                            machine.setState(MachineState.HAS_NOT_CONTRACT_NOT_7003);
                        }
                    } else {
                        System.out.println(machine.getPrefix());
                        // TODO: 3/14/2023 log tashlash kerak telegramga
                        return;
                    }
                } else if (machine.getTerminalId() == null && machine.getMerchantId() == null && machine.getInstId() != null) {
                    if (validPrefix != null) {
                        if (validPrefix.getValid()) {
                            machine.setState(MachineState.HAS_NOT_CONTRACT_STAY_WAREHOUSE);
                        } else {
                            machine.setState(MachineState.HAS_NOT_CONTRACT_NOT_7003);
                        }
                    } else {
                        System.out.println(machine.getPrefix());
                        // TODO: 3/14/2023 log tashlash kerak telegramga
                        return;
                    }
                }

            }
            machineRepository.save(machine);
        } else {
            if (validPrefix == null) {
                // TODO: 3/14/2023 telegramga log tashlash kerak
                System.out.println(machine.getPrefix());
                return;
            }

            if (!machine.getInstId().equals(oldMachine.getInstId())) {
                machineHistoryService.createChangeInst(oldMachine, machine);
                return;
            }
            if(machine.getIsContract())
//            if (machine.getIsContract()) {
//                if (validPrefix.getValid())
//                    if ((machine.getTerminalId() == null && machine.getMerchantId() != null) || (machine.getTerminalId() != null && machine.getMerchantId() == null)) {
//                        // TODO: 3/14/2023
//                        System.out.println("xato ketgan " + machine.getSrNumber());
//                    } else if (machine.getTerminalId() != null && (oldMachine.getState().equals(MachineState.HAS_CONTRACT_STAY_WAREHOUSE)|| (oldMachine.getState().equals(MachineState.HAS_NOT_CONTRACT_STAY_WAREHOUSE)))) {
//                        oldMachine.setMerchantId(machine.getMerchantId());
//                        oldMachine.setTerminalId(machine.getTerminalId());
//                        oldMachine.setBranch(machine.getBranch());
//                        oldMachine.setState(MachineState.HAS_CONTRACT_WITH_7003);
//                    } else if (machine.getBranch() != null && !machine.getBranchMfo().equals(oldMachine.getBranchMfo())) {
//                        oldMachine.setBranch(machine.getBranch());
//                        oldMachine.setBranchMfo(machine.getBranchMfo());
//                    } else {
//                        System.out.println("old machine  =  >>   " + oldMachine);
//                        System.out.println("new or changed machine  =  >> " + machine);
//                    }
//            } else {
//                oldMachine.setState(MachineState.HAS_NOT_CONTRACT_WORKING_7003);
//            }
//            if (machine.getIsContract() && (oldMachine.getState().equals(MachineState.HAS_CONTRACT_WITH_7003) || oldMachine.getState().equals(MachineState.HAS_CONTRACT_STAY_WAREHOUSE))) {
//                oldMachine.setState(MachineState.HAS_CONTRACT_NOT_7003);
//            } else {
//                oldMachine.setState(MachineState.HAS_NOT_CONTRACT_NOT_7003);
//            }

            machineRepository.save(oldMachine);
        }
    }

}
