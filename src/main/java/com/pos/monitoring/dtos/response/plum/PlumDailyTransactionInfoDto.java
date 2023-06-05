package com.pos.monitoring.dtos.response.plum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlumDailyTransactionInfoDto {

    private String requestId;

    private Integer totalCount;
    private Double totalDebit;

    private Double totalCredit;
}
