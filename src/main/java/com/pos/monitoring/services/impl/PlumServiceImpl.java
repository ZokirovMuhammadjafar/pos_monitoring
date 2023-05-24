package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.request.plum.PDailyTransactionRequestDto;
import com.pos.monitoring.dtos.response.plum.PDailyTransactionDto;
import com.pos.monitoring.dtos.response.plum.PDailyTransactionResponseDto;
import com.pos.monitoring.dtos.response.plum.PlumDailyTransactionCountDto;
import com.pos.monitoring.dtos.response.plum.PlumDailyTransactionCountsDto;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.MachineState;
import com.pos.monitoring.entities.Transaction;
import com.pos.monitoring.repositories.MachineRepository;
import com.pos.monitoring.repositories.TransactionRepository;
import com.pos.monitoring.services.PlumService;
import com.pos.monitoring.services.system.RestTemplates;
import com.pos.monitoring.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
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
    private final TransactionRepository transactionRepository;
    @Value("${plum.total-amount-url}")
    public String TOTAL_AMOUNT_URL;
    @Value("${plum.total-count-url}")
    public String TOTAL_COUNT_URL;
    @Value("${plum.organizationInn}")
    public String ORGANIZATION_INN;
    Logger logger = LogManager.getLogger(PlumServiceImpl.class);

    @Override
    public void dailySynchronizeAmount() {
        List<Machine> machines = machineRepository.findAllByState(MachineState.HAS_CONTRACT_WITH_7003);
        if (machines.isEmpty()) {
            return;
        }

        Map<String, String> headerData = new HashMap<>();
        headerData.put(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = convertToBody();

        for (Machine machine : machines) {
            if (ObjectUtils.isEmpty(machine.getTerminalId()) || ObjectUtils.isEmpty(machine.getMerchantId())) {
                continue;
            }

            PDailyTransactionRequestDto requestItemDto = new PDailyTransactionRequestDto(machine.getTerminalId(), machine.getMerchantId());

            body.put("terminals", List.of(requestItemDto));
            ResponseEntity<PDailyTransactionResponseDto> responseEntity = restTemplates.executeWithBasic(TOTAL_AMOUNT_URL, HttpMethod.POST, headerData, body, PDailyTransactionResponseDto.class);
            PDailyTransactionResponseDto responseDto = responseEntity.getBody();

            if (!ObjectUtils.isEmpty(responseDto)) {
                List<PDailyTransactionDto> amounts = responseDto.getData().getAmounts();
                Transaction transaction = new Transaction("test", "test-parent", "92408348", "90488368", amounts.get(0).getTotalAmount(), TimeUtils.toYYYYmmDD(new Date()));
                transactionRepository.save(transaction);
            }
        }
    }

    @Override
    public void getDailyTransactionDetail() {

        Map<String, Object> body = convertToBody();
    }

    @Override
    @Transactional
    public void getDailyTransactionCount() {
        Map<String, String> header = getHeader();
        Map<String, Object> body = convertToBody();

        List<Machine> machines = machineRepository.findAllByState(MachineState.HAS_CONTRACT_WITH_7003);
        if (machines.isEmpty()) {
            return;
        }

        PDailyTransactionRequestDto requestItemDto = new PDailyTransactionRequestDto();

        for (int i = 0; i < machines.size(); i++) {
            Machine machine = machines.get(i);
            if (!ObjectUtils.isEmpty(machine.getTerminalId()) && !ObjectUtils.isEmpty(machine.getMerchantId())) {
                long begin=System.currentTimeMillis();
                logger.info("sending request to plum machine sr_number = {} terminal_id={} merchant_id={} item={}", machine.getSrNumber(), machine.getTerminalId(), machine.getMerchantId(),i);
                sendAndSaveTransaction(header, body, requestItemDto, machine);
                logger.info("request have finished time = {}",System.currentTimeMillis()-begin);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendAndSaveTransaction(Map<String, String> header, Map<String, Object> body, PDailyTransactionRequestDto requestItemDto, Machine machine) {
        requestItemDto.setMerchantId(machine.getMerchantId());
        requestItemDto.setTerminalId(machine.getTerminalId());
        body.put("terminals", List.of(requestItemDto));
        ResponseEntity<PlumDailyTransactionCountsDto> responseEntity = restTemplates.executeWithBasic(TOTAL_COUNT_URL, HttpMethod.POST, header, body, PlumDailyTransactionCountsDto.class);
        PlumDailyTransactionCountsDto responseBody = responseEntity.getBody();
        if (!ObjectUtils.isEmpty(responseBody)) {
            PlumDailyTransactionCountDto data = responseBody.getData();
            Integer totalCount = data.getTotalCount();
            machine.setTransactionCount(totalCount);
            machine.setTransactionDate(new Date());
            machineRepository.saveAndFlush(machine);
        }
    }

    private Map<String, String> getHeader() {
        Map<String, String> headerData = new HashMap<>();
        headerData.put(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headerData;
    }

    private Map<String, Object> convertToBody() {
        Date yesterday = TimeUtils.minus(new Date(), Calendar.DATE, 1);
        Map<String, Object> body = new HashMap<>();
        body.put("organizationTin", ORGANIZATION_INN);
        body.put("dateFrom", TimeUtils.fromDate(yesterday));
        body.put("dateTo", TimeUtils.toDate(yesterday));
        return body;
    }
}
