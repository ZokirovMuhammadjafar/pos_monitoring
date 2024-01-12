package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.request.MachineFilterDto;
import com.pos.monitoring.dtos.request.StatisticDto;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.entities.Branch;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.enums.MachineState;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.repositories.BranchRepository;
import com.pos.monitoring.repositories.MachineRepository;
import com.pos.monitoring.repositories.TransactionInfoRepository;
import com.pos.monitoring.repositories.system.Connection8005;
import com.pos.monitoring.services.BranchService;
import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private final Connection8005 connection8005;
    private final MachineRepository machineRepository;
    private final BranchRepository branchRepository;
    private final BranchService branchService;
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
                    logger.error("bank topilmadi  ===  >>>  {}", branchMfo);
                    continue;
                }
                machine.setBranch(branch);
            }
            if (machine.getSrNumber() != null && machine.getSrNumber().length() <= 3) {
                logger.error("machinada xatolik === >>>  {}", machine.toString());
                continue;
            }
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
            stateChoose(machine);
        }
    }


    @Override
    public SingleResponse getStatistic(StatisticDto dto) {
        List<Map<String, Object>> statisticByMfos = machineRepository.getStatisticByMfos(dto.getMfos());
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Map<String, Long> stats = transactionInfoRepository.sumAllStatAndMfoIn(dto.getMfos(), yesterday);
        Optional<Integer> allMcc = transactionInfoRepository.countAllMcc(dto.getMfos());
        Map<String, Long> map = new HashMap<>();
        map.put("allTerminal", 0L);
        map.put("notWorking", 0L);
        map.put("working", 0L);
        map.put("hasContractTerminal", 0L);
        for (Map<String, Object> objectMap : statisticByMfos) {
            MachineState state = (MachineState) objectMap.get("state");
            Long number = (Long) objectMap.get("number");
            convert(map, state, number);
        }
        map.putAll(stats);
        map.put("offCount", (map.get("working") - stats.get("onCount")));
        allMcc.ifPresent(integer -> map.put("kassa_terminals", integer.longValue()));
        return SingleResponse.of(map);
    }

    @Override
    public ListResponse getInformationByInstId(MachineFilterDto filterDto) {
        if (filterDto.getMfos().isEmpty()) {
            throw new ValidatorException("MFOS_IS_NOT_COME");
        }
        List<Map<String, String>> machinesByInstIdOrMfos = machineRepository.getbyMfoList(filterDto.getMfos().stream().skip(filterDto.getPageNumber() / filterDto.getPageSize()).limit(filterDto.getPageSize()).toList());
        return ListResponse.of
                (
                        machinesByInstIdOrMfos, filterDto.getMfos().size()
                );
    }

    @Override
    @Transactional
    public void updateAllMachineTransactionStatus(List<String> parentMfos) {
        List<String> mfos = branchService.getBranches(parentMfos, true).stream().map(Branch::getMfo).collect(Collectors.toList());
        machineRepository.updateAllTransactionStatus(mfos);
    }

    private void convert(Map<String, Long> map, MachineState state, Long count) {
        switch (state) {
            case HAS_CONTRACT_WITH_7003 -> {//0
                map.put("working", map.get("working") + count);
                map.put("allTerminal", map.get("allTerminal") + count);
                map.put("hasContractTerminal", map.get("hasContractTerminal") + count);
            }
            case HAS_CONTRACT_STAY_WAREHOUSE -> {//4
                map.put("notWorking", map.get("notWorking") + count);
                map.put("allTerminal", map.get("allTerminal") + count);
                map.put("hasContractTerminal", map.get("hasContractTerminal") + count);
            }
            case HAS_NOT_CONTRACT_WORKING_7003 -> {//3
                map.put("allTerminal", map.get("allTerminal") + count);
                map.put("working", map.get("working") + count);
            }
        }
    }

    private void stateChoose(Machine machine) {
        Machine oldMachine = machineRepository.findBySrNumber(machine.getSrNumber());

        if (oldMachine == null) {
            create(machine);
            machineRepository.saveAndFlush(machine);
        } else {
            if (machine.isDeleted()) {
                oldMachine.setDeleted(true);
                return;
            }
            if (!machine.getInstId().equals(oldMachine.getInstId())) {
                oldMachine.setInstId(machine.getInstId());
            }
            if (!machine.getBranchMfo().equals(oldMachine.getBranchMfo())) {
                oldMachine.setBranchMfo(machine.getBranchMfo());
                oldMachine.setBranch(machine.getBranch());
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
            } else {
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
