package com.pos.monitoring.services;

import com.pos.monitoring.dtos.request.StatisticDto;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.dtos.request.MachineFilterDto;

import java.util.List;

public interface MachineService {
    void synchronizeDailyChanges(int i);

    void synchronizeDailyChangesWithBanksChosen(int i);

    void deleteByPrefix(String prefix);


    SingleResponse getStatistic(StatisticDto dto);

    ListResponse getInformationByInstId(MachineFilterDto filterDto);

    void updateAllMachineTransactionStatus(List<String>parentMfos);
}
