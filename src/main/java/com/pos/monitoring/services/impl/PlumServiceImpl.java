package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.request.plum.PDailyTransactionRequestDto;
import com.pos.monitoring.dtos.response.plum.PlumDailyTransactionCountsDto;
import com.pos.monitoring.dtos.response.plum.PlumDailyTransactionInfoDto;
import com.pos.monitoring.entities.DailySynchronize;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.dtos.enums.MachineState;
import com.pos.monitoring.entities.TransactionInfo;
import com.pos.monitoring.repositories.DailySynchronizeRepository;
import com.pos.monitoring.repositories.MachineRepository;
import com.pos.monitoring.repositories.TransactionInfoRepository;
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

    private final RestTemplates restTemplates;
    private final MachineRepository machineRepository;
    private final TransactionInfoRepository transactionInfoRepository;
    private final DailySynchronizeRepository dailySynchronizeRepository;
    @Value("${plum.total-count-url}")
    public String TOTAL_COUNT_URL;
    @Value("${plum.organizationInn}")
    public String ORGANIZATION_INN;
    private static final Logger logger = LogManager.getLogger(PlumServiceImpl.class);

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void getDailyTransactionInfo() {
        logger.info("------------ Transaction count start synchronization------------");
        Date today = new Date();

        Date yesterday = TimeUtils.minus(today, Calendar.DATE, 1);
        String todayAsString = TimeUtils.toYYYYmmDD(today);

        Map<String, String> header = getHeader();
        Map<String, Object> body = convertToBody(yesterday);

        DailySynchronize dailySynchronize;
        Optional<DailySynchronize> dailySynchronizeOptional = dailySynchronizeRepository.findByToday(todayAsString);
        int countAllByState = machineRepository.countAllByState(MachineState.HAS_CONTRACT_WITH_7003);
        int cycles = Math.round((float) countAllByState / 10);

        if (dailySynchronizeOptional.isEmpty()) {
            dailySynchronize = new DailySynchronize(todayAsString, countAllByState, cycles, 0);
            dailySynchronizeRepository.save(dailySynchronize);
        } else {
            dailySynchronize = dailySynchronizeOptional.get();
            if (dailySynchronize.getCycle() == dailySynchronize.getCycles()) {
                logger.info("------------ Transaction count end synchronization------------");
                return;
            }
        }

        for (int cycle = dailySynchronize.getCycle(); cycle < dailySynchronize.getCycles(); cycle++) {
            List<Machine> machines = machineRepository.findAllByStateOrderByIdAsc(MachineState.HAS_CONTRACT_WITH_7003, PageRequest.of(cycle, 10));
            if (machines.isEmpty()) {
                return;
            }
            PDailyTransactionRequestDto requestItemDto = new PDailyTransactionRequestDto();
            for (Machine machine : machines) {
                if (!ObjectUtils.isEmpty(machine.getTerminalId()) && !ObjectUtils.isEmpty(machine.getMerchantId())) {
                    long begin = System.currentTimeMillis();
                    logger.info("sending request to plum machine sr_number = {} terminal_id={} merchant_id={} item={}", machine.getSrNumber(), machine.getTerminalId(), machine.getMerchantId(), cycle);

                    try {
                        sendAndSaveTransaction(header, body, requestItemDto, machine, today, todayAsString, yesterday);
                    } catch (Exception e) {
                        dailySynchronize.setCycles(cycles);
                        dailySynchronize.setCycle(cycle);
                        dailySynchronizeRepository.save(dailySynchronize);
                        logger.info(e.getMessage());
                    }
                    logger.info("request have finished time = {}", System.currentTimeMillis() - begin);
                }
            }
        }
        dailySynchronize.setCycles(cycles);
        dailySynchronize.setCycle(cycles);
        dailySynchronizeRepository.save(dailySynchronize);
        logger.info("------------ Transaction count end synchronization------------");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendAndSaveTransaction(Map<String, String> header, Map<String, Object> body, PDailyTransactionRequestDto requestItemDto, Machine machine, Date today, String todayAsString, Date yesterday) {
        requestItemDto.setMerchantId(machine.getMerchantId());
        requestItemDto.setTerminalId(machine.getTerminalId());
        body.put("terminals", List.of(requestItemDto));
        ResponseEntity<PlumDailyTransactionCountsDto> responseEntity = restTemplates.executeWithBasic(TOTAL_COUNT_URL, HttpMethod.POST, header, body, PlumDailyTransactionCountsDto.class);
        PlumDailyTransactionCountsDto responseBody = responseEntity.getBody();
        if (!ObjectUtils.isEmpty(responseBody)) {
            PlumDailyTransactionInfoDto data = responseBody.getData();
            if (data.getTotalCount() != 0) {
                machine.setTransactionCount(data.getTotalCount());
                machine.setTransactionDebit(data.getTotalDebit());
                machine.setTransactionDate(today);
                machineRepository.saveAndFlush(machine);

                TransactionInfo transactionInfo = TransactionInfo.build(machine, data, todayAsString, yesterday);
                transactionInfoRepository.saveAndFlush(transactionInfo);
            }
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
