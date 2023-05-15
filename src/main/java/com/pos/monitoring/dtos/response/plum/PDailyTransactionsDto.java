package com.pos.monitoring.dtos.response.plum;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PDailyTransactionsDto {

    private String requestId;
    private List<PDailyTransactionDto> amounts;
}
