package com.pos.monitoring.dtos.response;

import com.pos.monitoring.entities.AbstractEntity;
import com.pos.monitoring.entities.enums.CalculateType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionCalculateDTO extends AbstractEntity {
    protected String mfo;
    protected Double amount;
    protected int total;
    protected CalculateType calculateType;
}
