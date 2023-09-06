package com.pos.monitoring.services;

import com.pos.monitoring.dtos.request.StatisticDto;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.dtos.pageable.MachineFilterDto;
import org.springframework.data.domain.PageRequest;

import java.util.HashMap;

public interface MachineService {
    void synchronizeDailyChanges(int i);

    void synchronizeDailyChangesWithBanksChosen(int i);


    SingleResponse getStatistic(StatisticDto dto);

    ListResponse getInformationByInstId(MachineFilterDto filterDto);

    void synchronizeTransactionFalse();

    void changesynchronizeType(PageRequest pageRequest, HashMap<String,Double>amountTransaction);

}
