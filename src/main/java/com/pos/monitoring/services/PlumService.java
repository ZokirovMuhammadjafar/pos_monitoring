package com.pos.monitoring.services;

import com.pos.monitoring.entities.enums.SynchronizeType;

import java.util.List;

public interface PlumService {

    void getDailyTransaction(SynchronizeType synchronizeType);
    void getDailyTransaction(List<String> mfos);
}
