package com.pos.monitoring.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "transactions")
public class Transaction extends AbstractEntity {

    @Column(nullable = false)
    protected String mfo;

    @Column(nullable = false)
    protected String parentMfo;

    @Column(nullable = false)
    protected String terminalId;

    @Column(nullable = false)
    protected String merchantId;

    protected Double amount;

    @Column(nullable = false)
    protected String date;
}
