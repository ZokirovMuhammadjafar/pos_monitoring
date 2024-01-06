package com.pos.monitoring.dtos.response;

import com.pos.monitoring.entities.AbstractEntity;
import com.pos.monitoring.entities.enums.CalculateType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
public class TransactionCalculateDTO extends AbstractEntity {
    protected String mfo;
    protected String today;
    protected Double amount;
    protected int total;
    protected CalculateType calculateType;
}
