package com.pos.monitoring.dtos.response.plum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PDailyTransactionResponseDto extends PlumBaseDto {

    private PDailyTransactionsDto data;
}
