package com.pos.monitoring.dtos.response.plum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlumDailyTransactionCountsDto extends PlumBaseDto {

    private PlumDailyTransactionCountDto data;

}
