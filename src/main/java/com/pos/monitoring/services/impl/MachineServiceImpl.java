package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.pageable.MachineFilterDto;
import com.pos.monitoring.dtos.request.StatisticDto;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.entities.Branch;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.enums.MachineState;
import com.pos.monitoring.repositories.BranchRepository;
import com.pos.monitoring.repositories.DailyTerminalInfoRepository;
import com.pos.monitoring.repositories.MachineRepository;
import com.pos.monitoring.repositories.TransactionInfoRepository;
import com.pos.monitoring.repositories.system.Connection8005;
import com.pos.monitoring.services.MachineHistoryService;
import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.utils.TimeUtils;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private final Connection8005 connection8005;
    private final MachineRepository machineRepository;
    private final BranchRepository branchRepository;
    private final MachineHistoryService machineHistoryService;
    private final DailyTerminalInfoRepository dailyTerminalInfoRepository;
    private final TransactionInfoRepository transactionInfoRepository;
    Logger logger = LogManager.getLogger(MachineServiceImpl.class);

    private synchronized static void create(Machine machine) {
        if (machine.getIsContract()) {
            if (machine.getTerminalId() != null && machine.getMerchantId() != null && machine.getInstId() != null) {
                if (machine.getStatus().equals("A")) {
                    machine.setState(MachineState.HAS_CONTRACT_WITH_7003);
                } else {
                    machine.setState(MachineState.HAS_CONTRACT_NOT_7003);
                }
            } else if (machine.getTerminalId() == null && machine.getMerchantId() == null && machine.getInstId() != null) {
                if (machine.getStatus().equals("A")) {
                    machine.setState(MachineState.HAS_CONTRACT_STAY_WAREHOUSE);
                } else {
                    machine.setState(MachineState.HAS_CONTRACT_NOT_7003);
                }
            }
        } else {
            if (machine.getTerminalId() != null && machine.getMerchantId() != null && machine.getInstId() != null) {
                if (machine.getStatus().equals("A")) {
                    machine.setState(MachineState.HAS_NOT_CONTRACT_WORKING_7003);
                } else {
                    machine.setState(MachineState.HAS_NOT_CONTRACT_NOT_7003);
                }
            } else if (machine.getTerminalId() == null && machine.getMerchantId() == null && machine.getInstId() != null) {
                if (machine.getStatus().equals("A")) {
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
    public void synchronizeDailyChangesWithBanksChosen(int i) {
        List<Machine> allChangeMachines = connection8005.getAllMachinesChangeWithBanksChosen(i);
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
    public void deleteByPrefix(String prefix) {
        machineRepository.deleteByPrefix(prefix);
    }

    @Override
    public SingleResponse getStatistic(StatisticDto dto) {
        List<Map<String, Object>> statisticByMfos = machineRepository.getStatisticByMfos(dto.getMfos());
        String today = TimeUtils.toYYYYmmDD(new Date());
        int workingCount = transactionInfoRepository.countAllByTodayAndMfoIn(today, dto.getMfos());
        int transactionCount = transactionInfoRepository.sumAllByTodayAndMfoIn(today, dto.getMfos());
        Map<String, Long> map = new HashMap<>();
        map.put("allTerminal", 0L);
        map.put("notWorking", 0L);
        map.put("working", 0L);
        map.put("hasContractTerminal", 0L);
        for (Map<String, Object> objectMap : statisticByMfos) {
            Short state = (Short) objectMap.get("state");
            Long number = (Long) objectMap.get("number");
            convert(map, MachineState.values()[state], number);
        }
        map.put("onCount", (long) workingCount);
        map.put("offCount", (map.get("working") - workingCount));
        map.put("transaction", (long) transactionCount);
        return SingleResponse.of(map);
    }

    @Override
    public ListResponse getInformationByInstId(MachineFilterDto filterDto) {
        List<Map<String, String>> instId = null;
        if (filterDto.getInstId() != null) {
            instId = machineRepository.getByInstId(filterDto.getInstId());
        } else if (filterDto.getMfos() != null && !filterDto.getMfos().isEmpty()) {
            instId = machineRepository.getbyMfoList(filterDto.getMfos());
        }
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
        Machine oldMachine = machineRepository.findBySrNumberAndDeleted(machine.getSrNumber(), false);
        if (oldMachine == null) {
            create(machine);
            machineRepository.saveAndFlush(machine);
        } else {
            if (!machine.getInstId().equals(oldMachine.getInstId())) {
                machineHistoryService.createChangeInst(oldMachine, machine);
                oldMachine.setBranch(machine.getBranch());
                oldMachine.setBranchMfo(machine.getBranchMfo());
            }
            if (machine.getStatus().equals("A")) {
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
            }
            else {
                if (oldMachine.getState().equals(MachineState.HAS_CONTRACT_NOT_7003) && !machine.getIsContract()) {
                    oldMachine.setState(MachineState.HAS_NOT_CONTRACT_NOT_7003);
                } else if (oldMachine.getState().equals(MachineState.HAS_NOT_CONTRACT_NOT_7003) && machine.getIsContract()) {
                    oldMachine.setState(MachineState.HAS_CONTRACT_NOT_7003);
                }
            }
            oldMachine.setMcc(machine.getMcc());
            oldMachine.setMerchantName(machine.getMerchantName());
            oldMachine.setStatus(machine.getStatus());
            oldMachine.setUpdateDate(new Date());
            oldMachine.setSoft(machine.getSoft());
            oldMachine.setModel(machine.getModel());
            machineRepository.saveAndFlush(oldMachine);
        }
    }
}
