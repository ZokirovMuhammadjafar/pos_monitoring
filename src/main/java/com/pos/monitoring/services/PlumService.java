package com.pos.monitoring.services;

public interface PlumService {

    void dailySynchronizeAmount();

    void getDailyTransactionDetail();

    void getDailyTransactionCount();
}
