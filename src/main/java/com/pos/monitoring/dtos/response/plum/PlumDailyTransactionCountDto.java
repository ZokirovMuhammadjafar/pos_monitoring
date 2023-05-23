package com.pos.monitoring.dtos.response.plum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlumDailyTransactionCountDto {

    private String requestId;

    private Integer totalCount;
}
