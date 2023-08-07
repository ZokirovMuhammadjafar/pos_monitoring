package com.pos.monitoring.services;

public interface PlumService {

    void getDailyTransactionInfoTypical();
    void getDailyTransactionInfoCassiaTerminals();

    void calculateTransactionAndCount();
}
