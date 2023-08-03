package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.pageable.TransactionCalculatePageableSearch;
import com.pos.monitoring.entities.TransactionCalculate;
import com.pos.monitoring.entities.enums.CalculateType;
import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.repositories.TransactionCalculateRepository;
import com.pos.monitoring.repositories.TransactionInfoRepository;
import com.pos.monitoring.services.TransactionCalculateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionCalculateServiceImpl implements TransactionCalculateService {

    private final TransactionCalculateRepository transactionCalculateRepository;
    private final TransactionInfoRepository transactionInfoRepository;

    @Override
    public List<TransactionCalculate> getAll(TransactionCalculatePageableSearch search) {
        if (ObjectUtils.isEmpty(search.getMfos())) {
            throw new ValidatorException("DONT_COME_MFOS");
        }
        Calendar calendar = Calendar.getInstance();
        if (search.getCalculateType().equals(CalculateType.WEEKLY)) {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        } else {
            calendar.add(Calendar.MONTH, -1);
        }

        List<TransactionCalculate> allByTransactionFromToDate = transactionInfoRepository.getAllByTransactionFromToDate(calendar.getTime(), new Date(), search.getMfos())
                .stream()
                .map((a) -> {
                    TransactionCalculate transactionCalculate = new TransactionCalculate();
                    transactionCalculate.setAmount((Double) a.get("amount"));
                    transactionCalculate.setTotal(Integer.parseInt(a.get("total") + ""));
                    transactionCalculate.setMfo("" + a.get("mfo"));
                    transactionCalculate.setId(System.currentTimeMillis());
                    return transactionCalculate;
                }).collect(Collectors.toList());
        return allByTransactionFromToDate;

    }
}
