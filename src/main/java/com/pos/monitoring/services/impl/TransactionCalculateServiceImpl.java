package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.request.TransactionCalculatePageableSearch;
import com.pos.monitoring.dtos.response.TransactionCalculateDTO;
import com.pos.monitoring.entities.enums.CalculateType;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.repositories.TransactionInfoRepository;
import com.pos.monitoring.services.TransactionCalculateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionCalculateServiceImpl implements TransactionCalculateService {


    private final TransactionInfoRepository transactionInfoRepository;

    @Override
    public List<TransactionCalculateDTO> getAll(TransactionCalculatePageableSearch search) {
        if (ObjectUtils.isEmpty(search.getMfos())) {
            throw new ValidatorException("DONT_COME_MFOS");
        }
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate;
        if (search.getCalculateType().equals(CalculateType.WEEKLY)) {
            fromDate = toDate.minusWeeks(1);
        } else {
            fromDate = toDate.minusMonths(1);
        }

        return transactionInfoRepository.getAllByTransactionFromToDate(fromDate, toDate, search.getMfos())
                .stream()
                .map((a) -> {
                    TransactionCalculateDTO transactionCalculate = new TransactionCalculateDTO();
                    transactionCalculate.setAmount((Double) a.get("amount"));
                    transactionCalculate.setTotal(Integer.parseInt(a.get("total") + ""));
                    transactionCalculate.setMfo("" + a.get("mfo"));
                    transactionCalculate.setId(System.currentTimeMillis());
                    return transactionCalculate;
                }).collect(Collectors.toList());


    }
}
