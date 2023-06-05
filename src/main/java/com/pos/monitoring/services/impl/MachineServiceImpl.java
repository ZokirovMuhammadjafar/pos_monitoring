package com.pos.monitoring.services.impl;

import com.pos.monitoring.entities.enums.MachineState;
import com.pos.monitoring.entities.enums.Soft;
import com.pos.monitoring.dtos.pageable.MachineFilterDto;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.entities.Branch;
import com.pos.monitoring.entities.DailyTerminalInfo;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.entities.enums.DailyStatus;
import com.pos.monitoring.entities.enums.MachineState;
import com.pos.monitoring.entities.enums.Soft;
import com.pos.monitoring.repositories.BranchRepository;
import com.pos.monitoring.repositories.DailyTerminalInfoRepository;
import com.pos.monitoring.repositories.MachineRepository;
import com.pos.monitoring.repositories.TerminalModelRepository;
import com.pos.monitoring.repositories.system.Connection8005;
import com.pos.monitoring.services.MachineHistoryService;
import com.pos.monitoring.services.MachineService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private final Connection8005 connection8005;
    private final MachineRepository machineRepository;
    private final BranchRepository branchRepository;
    private final MachineHistoryService machineHistoryService;
    private final TerminalModelRepository terminalModelRepository;
    private final DailyTerminalInfoRepository dailyTerminalInfoRepository;
    Logger logger = LogManager.getLogger(MachineServiceImpl.class);

    private synchronized static void create(Machine machine, TerminalModel validPrefix) {
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
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void synchronizeDailyChanges(int i) {
        List<Machine> allChangeMachines = connection8005.getAllMachinesChange(i);
        for (Machine machine : allChangeMachines) {
            String branchMfo = machine.getBranchMfo();
            if (branchMfo != null) {
                Branch branch = branchRepository.findByMfoAndDeletedFalse(branchMfo);
                if (branch == null) {
                    logger.error("branch topilmadi  ===  >>> {}", branchMfo);
                    continue;
                }
                machine.setBranch(branch);
            }
            if (machine.getSrNumber() != null && machine.getSrNumber().length() <= 3) {
                logger.error("machinada xatolik ===>>>>  {} ", machine.toString());
                continue;
            }
            machine.setPrefix(machine.getSrNumber().substring(0, 3));
            stateChoose(machine);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void synchronizeFix() {
        List<DailyTerminalInfo> dailyTerminalInfoFix = connection8005.getDailyTerminalInfoFix();
        for (DailyTerminalInfo terminalInfoFix : dailyTerminalInfoFix) {
            Machine machina = machineRepository.findBySrNumberAndDeleted(terminalInfoFix.getSrNumber(), false);
            terminalInfoFix.setMachine(machina);
            terminalInfoFix.setStatus(DailyStatus.FIXED);
            dailyTerminalInfoRepository.save(terminalInfoFix);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void synchronizeAuthCode() {
        List<DailyTerminalInfo> dailyTerminalInfoAuthCode = connection8005.getDailyTerminalInfoAuthCode();
        for (DailyTerminalInfo terminalInfoAuthCode : dailyTerminalInfoAuthCode) {
            Machine machina = machineRepository.findBySrNumberAndDeleted(terminalInfoAuthCode.getSrNumber(), false);
            terminalInfoAuthCode.setMachine(machina);
            terminalInfoAuthCode.setStatus(DailyStatus.AUTH_CODE);
            dailyTerminalInfoRepository.save(terminalInfoAuthCode);
        }
    }

    @Override
    public void deleteByPrefix(String prefix) {
        machineRepository.deleteByPrefix(prefix);
    }

    @Override
    public void updateValid(TerminalModel terminalModel) {
        List<Machine> machineStream = machineRepository.findPrefix(terminalModel.getPrefix()).collect(Collectors.toList());
        Executors.newFixedThreadPool(8).submit(() -> {
            machineStream.forEach(machine -> {
                create(machine, terminalModel);
                machine.setUpdateDate(new Date());
                machineRepository.save(machine);
            });
        });
    }

    @Override
    public SingleResponse getStat(String instId) {
        List<Map<String, Object>> stateMap = machineRepository.getState(instId);
        Map<String, Long> map = new HashMap<>();
        map.put("allTerminal", 0L);
        map.put("notWorking", 0L);
        map.put("working", 0L);
        map.put("hasContractTerminal", 0L);
        for (Map<String, Object> objectMap : stateMap) {
            Short state = (Short) objectMap.get("state");
            Long number = (Long) objectMap.get("number");
            convert(map, MachineState.values()[state], number);
        }
        return SingleResponse.of(map);
    }

    @Override
    public ListResponse getInformationByInstId(MachineFilterDto filterDto) {
        List<Map<String, String>> instId = machineRepository.getByInstId(filterDto.getInstId());
        int total = instId.size();
        return ListResponse.of
                (
                        instId
                                .stream()
                                .skip(filterDto.getPageNumber())
                                .limit(filterDto.getPageSize())
                                .collect(Collectors.toList()), total
                );
    }

    private void convert(Map<String, Long> map, MachineState state, Long count) {
        switch (state) {
            case HAS_CONTRACT_WITH_7003 -> {//0
                map.put("working", map.get("working") + count);
                map.put("allTerminal", map.get("allTerminal") + count);
                map.put("hasContractTerminal", map.get("hasContractTerminal") + count);
            }
            case HAS_CONTRACT_STAY_WAREHOUSE, HAS_CONTRACT_NOT_7003 -> {//4
                map.put("notWorking", map.get("notWorking") + count);
                map.put("allTerminal", map.get("allTerminal") + count);
                map.put("hasContractTerminal", map.get("hasContractTerminal") + count);
            }
            case HAS_NOT_CONTRACT_WORKING_7003 -> {//3
                map.put("allTerminal", map.get("allTerminal") + count);
            }
            case HAS_NOT_CONTRACT_STAY_WAREHOUSE -> {//6
                map.put("notContractNotWorking", count);
                map.put("allTerminal", map.get("allTerminal") + count);
            }
        }
    }

    private void stateChoose(Machine machine) {
        TerminalModel validPrefix = terminalModelRepository.findByPrefixAndDeleted(machine.getPrefix(), false);
        Machine oldMachine = machineRepository.findBySrNumberAndDeleted(machine.getSrNumber(), false);
        if (validPrefix == null) {
            logger.error("prefix yoq === >>> {} ", machine.getPrefix());
            return;
        }
        if (machine.getSoft() == null && validPrefix.getName().contains("920")) {
            machine.setSoft(Soft.UZPOS);
        }
        machine.setModel(validPrefix.getName());
        if (oldMachine == null) {
            create(machine, validPrefix);
            machineRepository.saveAndFlush(machine);
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
                    oldMachine.setState(MachineState.HAS_CONTRACT_NOT_7003);
                }
            }
            oldMachine.setUpdateDate(new Date());
            oldMachine.setSoft(machine.getSoft());
            oldMachine.setModel(machine.getModel());
            machineRepository.saveAndFlush(oldMachine);
        }
    }
}
