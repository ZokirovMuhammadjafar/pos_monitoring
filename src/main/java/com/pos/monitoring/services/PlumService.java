package com.pos.monitoring.services;

public interface PlumService {

    void dailySynchronizeAmount();

    void getDailyTransactionDetail(String terminalId, String merchantId);
}
