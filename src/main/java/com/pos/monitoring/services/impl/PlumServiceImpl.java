package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.pageable.BranchFilterDto;
import com.pos.monitoring.dtos.request.plum.PDailyTransactionRequestDto;
import com.pos.monitoring.dtos.response.plum.PlumDailyTransactionCountsDto;
import com.pos.monitoring.dtos.response.plum.PlumDailyTransactionInfoDto;
import com.pos.monitoring.entities.*;
import com.pos.monitoring.entities.enums.CalculateType;
import com.pos.monitoring.entities.enums.MachineState;
import com.pos.monitoring.entities.enums.SynchronizeType;
import com.pos.monitoring.repositories.*;
import com.pos.monitoring.repositories.system.specifications.MachineSpecification;
import com.pos.monitoring.services.BranchService;
import com.pos.monitoring.services.PlumService;
import com.pos.monitoring.services.system.RestTemplates;
import com.pos.monitoring.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Service
@RequiredArgsConstructor
public class PlumServiceImpl implements PlumService {

    private static final Logger logger = LogManager.getLogger(PlumServiceImpl.class);
    private final RestTemplates restTemplates;
    private final MachineRepository machineRepository;
    private final TransactionInfoRepository transactionInfoRepository;
    private final DailySynchronizeRepository dailySynchronizeRepository;
    private final BranchService branchService;
    private final TransactionCalculateRepository transactionCalculateRepository;
    @Value("${plum.total-count-url}")
    public String TOTAL_COUNT_URL;
    @Value("${plum.organizationInn}")
    public String ORGANIZATION_INN;

    private static Date previousTime(int dayOfMonth) {
        Calendar yesterdayCalendar = Calendar.getInstance();
        yesterdayCalendar.add(dayOfMonth, -1);
        Date yesterday = yesterdayCalendar.getTime();
        return yesterday;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void getDailyTransaction(SynchronizeType synchronizeType) {
//        logger.info("------------ Transaction count start synchronization------------");
//
//        Date today = new Date();
//
//        String todayAsString = TimeUtils.toYYYYmmDD(today);
//
//        DailySynchronize dailySynchronize = getDailySynchronize(todayAsString, synchronizeType);
//        if (dailySynchronize == null) return;
//        Date yesterday = previousTime(Calendar.DAY_OF_YEAR);
//
//        Map<String, String> header = getHeader();
//        Map<String, Object> body = convertToBody(yesterday);
//        for (int cycle = dailySynchronize.getCycle(), counter = 0; cycle < dailySynchronize.getCycles(); cycle++, counter++) {
//            List<Machine> machines = machineRepository.getAllMachineForTransactionRequest(synchronizeType, PageRequest.of(cycle, 10));
//            if (machines.isEmpty()) {
//                continue;
//            }
//            if (counter == 1000) {
//                return;
//            }
//            PDailyTransactionRequestDto requestItemDto = new PDailyTransactionRequestDto();
//            for (Machine machine : machines) {
//                if (!ObjectUtils.isEmpty(machine.getTerminalId()) && !ObjectUtils.isEmpty(machine.getMerchantId())) {
//                    long begin = System.currentTimeMillis();
//                    logger.info("sending request to plum machine sr_number = {} terminal_id={} merchant_id={} item={}", machine.getSrNumber(), machine.getTerminalId(), machine.getMerchantId(), cycle);
//                    try {
//                        sendAndSaveTransaction(header, body, requestItemDto, machine, today, todayAsString, yesterday);
//                    } catch (Exception e) {
//                        dailySynchronize.setCycle(cycle);
//                        dailySynchronizeRepository.save(dailySynchronize);
//                        logger.info(e.getMessage());
//                    }
//                    logger.info("request have finished time = {}", System.currentTimeMillis() - begin);
//                }
//            }
//            dailySynchronize.setCycle(cycle);
//            dailySynchronizeRepository.save(dailySynchronize);
//        }
//
//        dailySynchronize.setCycle(dailySynchronize.getCycles());
//        dailySynchronize.setCalculate(true);
//        dailySynchronizeRepository.save(dailySynchronize);
//        logger.info("------------ Transaction count end synchronization------------");
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void getDailyTransaction(List<String> mfos) {
        logger.info("------------ Transaction count start synchronization------------");
        Date today = new Date();
        String todayAsString = TimeUtils.toYYYYmmDD(today);
        Set<Branch> branches = branchService.getBranches(mfos, true);
        Date yesterday = previousTime(Calendar.DAY_OF_YEAR);
        Map<String, String> header = getHeader();
        Map<String, Object> body = convertToBody(yesterday);
        Iterator<Branch> iterator = branches.iterator();
        while (iterator.hasNext()) {
            Branch branch = iterator.next();
            List<Machine> machines = machineRepository.getAllMachineForTransactionRequest(branch.getMfo(), PageRequest.of(0, 10));
            while (machines.size() > 0) {
                PDailyTransactionRequestDto requestItemDto = new PDailyTransactionRequestDto();
                for (Machine machine : machines) {
                    if (!ObjectUtils.isEmpty(machine.getTerminalId()) && !ObjectUtils.isEmpty(machine.getMerchantId())) {
                        long begin = System.currentTimeMillis();
                        logger.info("sending request to plum machine sr_number = {} terminal_id={} merchant_id={}", machine.getSrNumber(), machine.getTerminalId(), machine.getMerchantId());
                        try {
                            sendAndSaveTransaction(header, body, requestItemDto, machine, today, todayAsString, yesterday);
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                        logger.info("request have finished time = {}", System.currentTimeMillis() - begin);
                    }
                }
                machines = machineRepository.getAllMachineForTransactionRequest(branch.getMfo(), PageRequest.of(0, 10));
            }
            System.gc();
        }
        logger.info("------------ Transaction count end synchronization------------");
    }

//    private List<DailySynchronize> getDailySynchronize(String todayAsString, SynchronizeType synchronizeType,List<String>mfos) {
//        DailySynchronize dailySynchronize;
//        Optional<DailySynchronize> dailySynchronizeOptional = dailySynchronizeRepository.findByTodayAndSynchronizationType(todayAsString, synchronizeType);
//        if (dailySynchronizeOptional.isPresent()) {
//            dailySynchronize = dailySynchronizeOptional.get();
//            if (dailySynchronize.getCycle() == dailySynchronize.getCycles()) {
//                logger.info("------------ Transaction count end synchronization------------");
//                return null;
//            }
//        } else {
//            long countAllByState = machineRepository.count(MachineSpecification.machineStatusIn(List.of(MachineState.HAS_CONTRACT_WITH_7003, MachineState.HAS_NOT_CONTRACT_WORKING_7003)).and(MachineSpecification.machinaSyncType(synchronizeType)));
//            int cycles = (int) Math.ceil((float) countAllByState / 10);
//            dailySynchronize = new DailySynchronize(todayAsString, (int)countAllByState, synchronizeType, cycles, 0,, Boolean.FALSE);
//            dailySynchronizeRepository.save(dailySynchronize);
//        }
//        return dailySynchronize;
//    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void sendAndSaveTransaction(Map<String, String> header, Map<String, Object> body, PDailyTransactionRequestDto requestItemDto, Machine old, Date today, String todayAsString, Date yesterday) {
        Machine machine = machineRepository.getById(old.getId());
        requestItemDto.setMerchantId(machine.getMerchantId());
        requestItemDto.setTerminalId(machine.getTerminalId());
        body.put("terminals", List.of(requestItemDto));
        ResponseEntity<PlumDailyTransactionCountsDto> responseEntity = restTemplates.executeWithBasic(TOTAL_COUNT_URL, HttpMethod.POST, header, body, PlumDailyTransactionCountsDto.class);
        PlumDailyTransactionCountsDto responseBody = responseEntity.getBody();
        if (!ObjectUtils.isEmpty(responseBody)) {
            PlumDailyTransactionInfoDto data = responseBody.getData();
            if (data.getTotalCount() != 0) {
                TransactionInfo transactionInfo = TransactionInfo.build(machine, data, todayAsString, yesterday);
                transactionInfoRepository.saveAndFlush(transactionInfo);
            }
            machine.setTransactionCount(data.getTotalCount());
            machine.setTransactionDebit(data.getTotalDebit());
            machine.setTransactionDate(yesterday);
            machine.setSyncedTransaction(true);
            machineRepository.saveAndFlush(machine);
        }
    }

    private Map<String, String> getHeader() {
        Map<String, String> headerData = new HashMap<>();
        headerData.put(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headerData;
    }

    private Map<String, Object> convertToBody(Date yesterday) {
        Map<String, Object> body = new HashMap<>();
        body.put("organizationTin", ORGANIZATION_INN);
        body.put("dateFrom", TimeUtils.fromDate(yesterday));
        body.put("dateTo", TimeUtils.toDate(yesterday));
        return body;
    }
}
