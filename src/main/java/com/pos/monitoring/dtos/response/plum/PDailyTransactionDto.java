package com.pos.monitoring.dtos.response.plum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PDailyTransactionDto {

    private String terminalId;
    private String merchantId;
    private Double totalCorpAmount;
    private Double totalPersAmount;
    private Double totalAmount;
}
