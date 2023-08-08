package com.pos.monitoring.services;

import com.pos.monitoring.entities.enums.SynchronizeType;

public interface PlumService {

    void getDailyTransaction(SynchronizeType synchronizeType);
}
