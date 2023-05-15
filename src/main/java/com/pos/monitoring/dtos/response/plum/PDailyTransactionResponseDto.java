package com.pos.monitoring.dtos.response.plum;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PDailyTransactionResponseDto implements Serializable {

    private boolean success;

    private PDailyTransactionsDto data;

    private int code;
}
