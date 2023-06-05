package com.pos.monitoring.services;

import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.dtos.pageable.MachineFilterDto;
import com.pos.monitoring.entities.TerminalModel;

public interface MachineService {
    void synchronizeDailyChanges(int i);

    void synchronizeFix();

    void synchronizeAuthCode();

    void deleteByPrefix(String prefix);

    void updateValid(TerminalModel terminalModel);

    SingleResponse getStat(String instId);

    ListResponse getInformationByInstId(MachineFilterDto filterDto);
}
