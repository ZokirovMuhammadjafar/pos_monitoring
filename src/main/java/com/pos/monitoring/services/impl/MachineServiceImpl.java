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
import java.util.concurrent.atomic.AtomicInteger;

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
                    System.out.println("branch topilmadi  ===  >>>"+branchMfo);
                    // TODO: 3/13/2023 shuni telegram bot orqali jonatib qoyish kerak mfo tid mid
                    continue;
                }
                machine.setBranch(branch);
            }
            if (machine.getSrNumber() != null && machine.getSrNumber().length() <= 3) {
                System.err.println("mavhinada xatolik ===>>>>   "+machine.toString());
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
        if (validPrefix == null) {
            // TODO: 3/14/2023 telegramga log tashlash kerak
            System.out.println(machine.getPrefix());
            return;
        }
        if (oldMachine == null) {
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
                    if (validPrefix.getValid()) {
                        machine.setState(MachineState.HAS_NOT_CONTRACT_WORKING_7003);
                    } else {
                        machine.setState(MachineState.HAS_NOT_CONTRACT_NOT_7003);
                    }
                } else if (machine.getTerminalId() == null && machine.getMerchantId() == null && machine.getInstId() != null) {
                    if (validPrefix.getValid()) {
                        machine.setState(MachineState.HAS_NOT_CONTRACT_STAY_WAREHOUSE);
                    } else {
                        machine.setState(MachineState.HAS_NOT_CONTRACT_NOT_7003);
                    }
                }
            }
            machineRepository.save(machine);
        } else {

            if (!machine.getInstId().equals(oldMachine.getInstId())) {
                machineHistoryService.createChangeInst(oldMachine, machine);
                return;
            }
            if (validPrefix.getValid()) {
                if (machine.getIsContract() && oldMachine.getIsContract()) {
                    if (oldMachine.getState().equals(MachineState.HAS_CONTRACT_STAY_WAREHOUSE) && machine.getMerchantId() != null && machine.getTerminalId() != null) {
                        oldMachine.setState(MachineState.HAS_CONTRACT_WITH_7003);
                        oldMachine.setTerminalId(machine.getTerminalId());
                        oldMachine.setMerchantId(machine.getMerchantId());
                    } else if (oldMachine.getState().equals(MachineState.HAS_CONTRACT_WITH_7003) && machine.getMerchantId() == null && machine.getTerminalId() == null) {
                        oldMachine.setState(MachineState.HAS_CONTRACT_STAY_WAREHOUSE);
                        oldMachine.setTerminalId(machine.getTerminalId());
                        oldMachine.setMerchantId(machine.getMerchantId());
                    }
                } else if (!machine.getIsContract() && !oldMachine.getIsContract()) {
                    if (oldMachine.getState().equals(MachineState.HAS_NOT_CONTRACT_STAY_WAREHOUSE) && machine.getMerchantId() != null && machine.getTerminalId() != null) {
                        oldMachine.setState(MachineState.HAS_NOT_CONTRACT_WORKING_7003);
                        oldMachine.setTerminalId(machine.getTerminalId());
                        oldMachine.setMerchantId(machine.getMerchantId());
                    } else if (oldMachine.getState().equals(MachineState.HAS_NOT_CONTRACT_WORKING_7003) && machine.getMerchantId() == null && machine.getTerminalId() == null) {
                        oldMachine.setState(MachineState.HAS_NOT_CONTRACT_STAY_WAREHOUSE);
                        oldMachine.setTerminalId(machine.getTerminalId());
                        oldMachine.setMerchantId(machine.getMerchantId());
                    }
                } else if (machine.getIsContract()) {
                    if (oldMachine.getState().equals(MachineState.HAS_NOT_CONTRACT_WORKING_7003) && machine.getMerchantId() == null && machine.getTerminalId() == null) {
                        oldMachine.setState(MachineState.HAS_CONTRACT_STAY_WAREHOUSE);
                        oldMachine.setTerminalId(machine.getTerminalId());
                        oldMachine.setMerchantId(machine.getMerchantId());
                    } else if (oldMachine.getState().equals(MachineState.HAS_NOT_CONTRACT_STAY_WAREHOUSE) && machine.getMerchantId() != null && machine.getTerminalId() != null) {
                        oldMachine.setState(MachineState.HAS_CONTRACT_WITH_7003);
                        oldMachine.setTerminalId(machine.getTerminalId());
                        oldMachine.setMerchantId(machine.getMerchantId());
                    } else if (oldMachine.getState().equals(MachineState.HAS_NOT_CONTRACT_WORKING_7003)) {
                        oldMachine.setState(MachineState.HAS_CONTRACT_WITH_7003);
                    } else if (oldMachine.getState().equals(MachineState.HAS_NOT_CONTRACT_STAY_WAREHOUSE)) {
                        oldMachine.setState(MachineState.HAS_CONTRACT_STAY_WAREHOUSE);
                    }
                } else {
                    if (oldMachine.getState().equals(MachineState.HAS_CONTRACT_WITH_7003) && machine.getMerchantId() == null && machine.getTerminalId() == null) {
                        oldMachine.setState(MachineState.HAS_NOT_CONTRACT_STAY_WAREHOUSE);
                        oldMachine.setTerminalId(machine.getTerminalId());
                        oldMachine.setMerchantId(machine.getMerchantId());
                    } else if (oldMachine.getState().equals(MachineState.HAS_CONTRACT_STAY_WAREHOUSE) && machine.getMerchantId() != null && machine.getTerminalId() != null) {
                        oldMachine.setState(MachineState.HAS_NOT_CONTRACT_WORKING_7003);
                        oldMachine.setTerminalId(machine.getTerminalId());
                        oldMachine.setMerchantId(machine.getMerchantId());
                    } else if (oldMachine.getState().equals(MachineState.HAS_CONTRACT_WITH_7003)) {
                        oldMachine.setState(MachineState.HAS_NOT_CONTRACT_WORKING_7003);
                    } else if (oldMachine.getState().equals(MachineState.HAS_CONTRACT_STAY_WAREHOUSE)) {
                        oldMachine.setState(MachineState.HAS_NOT_CONTRACT_STAY_WAREHOUSE);
                    }
                }
            } else {
                if (oldMachine.getState().equals(MachineState.HAS_CONTRACT_NOT_7003) && !machine.getIsContract()) {
                    oldMachine.setState(MachineState.HAS_NOT_CONTRACT_NOT_7003);
                } else if (oldMachine.getState().equals(MachineState.HAS_NOT_CONTRACT_NOT_7003) && machine.getIsContract()) {
                    oldMachine.setState(MachineState.HAS_NOT_CONTRACT_NOT_7003);
                }
            }
            machineRepository.save(oldMachine);
        }
    }

}
