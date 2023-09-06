package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.pageable.MachineFilterDto;
import com.pos.monitoring.dtos.request.StatisticDto;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.entities.Branch;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.enums.MachineState;
import com.pos.monitoring.entities.enums.SynchronizeType;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.repositories.BranchRepository;
import com.pos.monitoring.repositories.DailyTerminalInfoRepository;
import com.pos.monitoring.repositories.MachineRepository;
import com.pos.monitoring.repositories.TransactionInfoRepository;
import com.pos.monitoring.repositories.system.Connection8005;
import com.pos.monitoring.repositories.system.specifications.MachineSpecification;
import com.pos.monitoring.services.MachineHistoryService;
import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
                    logger.error("bank topilmadi  ===  >>>  {}", branchMfo);
                    continue;
                }
                machine.setBranch(branch);
            }
            if (machine.getSrNumber() != null && machine.getSrNumber().length() <= 3) {
                logger.error("machinada xatolik === >>>  {}", machine.toString());
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
    public SingleResponse getStatistic(StatisticDto dto) {
        List<Map<String, Object>> statisticByMfos = machineRepository.getStatisticByMfos(dto.getMfos());
        String today = TimeUtils.toYYYYmmDD(new Date(1692278095312L));
        int workingCount = transactionInfoRepository.countAllByTodayAndMfoIn(today, dto.getMfos());
        Optional<Integer> transactionCount = transactionInfoRepository.sumAllCountByTodayAndMfoIn(today, dto.getMfos());
        Optional<Double> transactionAmount = transactionInfoRepository.sumAllAmountByTodayAndMfoIn(today, dto.getMfos());
        Optional<Integer> allMcc = transactionInfoRepository.countAllMcc(dto.getMfos());

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
        transactionCount.ifPresent(integer -> map.put("transaction", integer.longValue()));
        transactionAmount.ifPresent(integer -> map.put("transaction_sum", (long) (integer/100_000_000)));
        allMcc.ifPresent(integer -> map.put("kassa_terminals", integer.longValue()));
        return SingleResponse.of(map);
    }

    @Override
    public ListResponse getInformationByInstId(MachineFilterDto filterDto) {
        if (filterDto.getMfos().isEmpty()) {
            throw new ValidatorException("MFOS_IS_NOT_COME");
        }
        List<Map<String, String>> machinesByInstIdOrMfos = machineRepository.getbyMfoList(filterDto.getMfos());
        int total = machinesByInstIdOrMfos.size();
        return ListResponse.of
                (
                        machinesByInstIdOrMfos
                                .stream()
                                .skip(filterDto.getPageNumber())
                                .limit(filterDto.getPageSize())
                                .collect(Collectors.toList()), total
                );
    }

    @Override
    public void synchronizeTransactionFalse() {
        machineRepository.synchTransactionFalse();
    }

    @Override
    public void changesynchronizeType(PageRequest pageRequest,HashMap<String,Double>amountTransaction) {
        Page<Machine> all = machineRepository.findAll(MachineSpecification.machineStatusIn(List.of(MachineState.HAS_CONTRACT_WITH_7003,MachineState.HAS_NOT_CONTRACT_WORKING_7003)),pageRequest);
        List<Machine> content = all.getContent();
        if (content.size()==0){
            return;
        }
        changeTypesMachine(content,amountTransaction);
        changesynchronizeType(pageRequest.next(),amountTransaction);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public  void changeTypesMachine(List<Machine> all,HashMap<String,Double>amountTransaction) {
        all.forEach(machine->{
            Double transactionDebit = amountTransaction.get(machine.getTerminalId().concat(machine.getMerchantId()));
            if(transactionDebit!=null){
                double v = transactionDebit / 100;
                if(machine.getSynchronizationType().equals(SynchronizeType.KASSA)||(machine.getMcc()!=null&&machine.getMcc().contains("6012 6010 6050"))){
                    machine.setSynchronizationType(SynchronizeType.KASSA);
                } else if(v<=1_000_000D&&v>=100_000D){
                    machine.setSynchronizationType(SynchronizeType.BETWEEN_HUNDRED_THOUSAND_AND_MILLION);
                }else if(v>1_000_000D){
                    machine.setSynchronizationType(SynchronizeType.GREATER_THEN_MILLION);
                }else if(v<1000_000D&&v>0){
                    machine.setSynchronizationType(SynchronizeType.LOWER_THAN_HUNDRED_THOUSAND);
                }else{
                    machine.setSynchronizationType(SynchronizeType.TYPICAL);
                }
            }
            machineRepository.saveAndFlush(machine);
        });
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
        Machine oldMachine = machineRepository.findBySrNumberAndDeleted(machine.getSrNumber(), false);
        if (oldMachine == null) {
            create(machine);
            machineRepository.saveAndFlush(machine);
        } else {
            if (!machine.getInstId().equals(oldMachine.getInstId())) {
                machineHistoryService.createChangeInst(oldMachine, machine);
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
