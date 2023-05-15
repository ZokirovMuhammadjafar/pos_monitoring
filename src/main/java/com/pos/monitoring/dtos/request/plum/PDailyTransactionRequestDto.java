package com.pos.monitoring.dtos.request.plum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PDailyTransactionRequestDto implements Serializable {

    private String terminalId;
    private String merchantId;
}
