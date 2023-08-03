package com.pos.monitoring.services;

public interface PlumService {

    void getDailyTransactionInfo();
    void getDailyTransactionInfoCassiaTerminals();

    void calculateTransactionAndCount();
}
