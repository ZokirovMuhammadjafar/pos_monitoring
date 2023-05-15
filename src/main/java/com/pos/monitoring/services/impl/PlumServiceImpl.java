package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.request.plum.PDailyTransactionRequestDto;
import com.pos.monitoring.dtos.response.plum.PDailyTransactionDto;
import com.pos.monitoring.dtos.response.plum.PDailyTransactionResponseDto;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.MachineState;
import com.pos.monitoring.entities.Transaction;
import com.pos.monitoring.repositories.MachineRepository;
import com.pos.monitoring.repositories.TransactionRepository;
import com.pos.monitoring.services.PlumService;
import com.pos.monitoring.services.system.RestTemplates;
import com.pos.monitoring.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Service
@RequiredArgsConstructor
public class PlumServiceImpl implements PlumService {

    @Value("${plum.total-amount-url}")
    public String TOTAL_AMOUNT_URL;

    @Value("${plum.organizationInn}")
    public String ORGANIZATION_INN;

    private final RestTemplates restTemplates;
    private final MachineRepository machineRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void dailySynchronizeAmount() {
        List<Machine> machines = machineRepository.findAllByState(MachineState.HAS_CONTRACT_WITH_7003);
        if (machines.isEmpty()) {
            return;
        }

        Map<String, String> headerData = new HashMap<>();
        headerData.put(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Date yesterday = TimeUtils.minus(new Date(), Calendar.DATE, 1);
        Map<String, Object> body = new HashMap<>();
        body.put("organizationTin", ORGANIZATION_INN);
        body.put("dateFrom", TimeUtils.fromDate(yesterday));
        body.put("dateTo", TimeUtils.toDate(yesterday));

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
    public void getDailyTransactionDetail(String terminalId, String merchantId) {
        if (ObjectUtils.isEmpty(terminalId) || ObjectUtils.isEmpty(merchantId)) {
            return;
        }

        Map<String, String> headerData = new HashMap<>();
        headerData.put(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Date yesterday = TimeUtils.minus(new Date(), Calendar.DATE, 1);
        Map<String, Object> body = new HashMap<>();
        body.put("organizationTin", ORGANIZATION_INN);
        body.put("dateFrom", TimeUtils.fromDate(yesterday));
        body.put("dateTo", TimeUtils.toDate(yesterday));
    }
}
