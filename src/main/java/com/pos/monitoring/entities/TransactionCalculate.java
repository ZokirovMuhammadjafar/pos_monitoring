package com.pos.monitoring.entities;

import com.pos.monitoring.entities.enums.CalculateType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_calculate")
@Deprecated(since = "2023-12-23 dan ochirldi bundan foydalanilmaydi")
public class TransactionCalculate extends AbstractEntity {

    @Column(nullable = false)
    protected String mfo;

    @Column
    protected String today;

    @Column
    protected Double amount;

    @Column
    protected int total;

    @Column
    @Enumerated(value = EnumType.ORDINAL)
    protected CalculateType calculateType;
}
