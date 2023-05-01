package com.pos.monitoring.services;

import com.pos.monitoring.dto.SingleResponse;
import com.pos.monitoring.entities.TerminalModel;

public interface MachineService {
    /**
     * this method use synchronize the machines from 8005
     * update comes every day
     */
    void synchronize();

    void deleteByPrefix(String prefix);

    void updateValid(TerminalModel terminalModel);



    SingleResponse getStat(String instId);
}
