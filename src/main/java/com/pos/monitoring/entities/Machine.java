package com.pos.monitoring.entities;

import com.pos.monitoring.entities.enums.MachineState;
import com.pos.monitoring.entities.enums.Soft;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "machines")
@ToString
public class Machine extends AbstractEntity {
    @Column
    protected String srNumber;
    @Column
    protected String instId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    protected Branch branch;
    @Column
    protected String branchMfo;
    @Column
    protected String merchantId;
    @Column
    protected String terminalId;
    @Enumerated(value = EnumType.STRING)
    protected Soft soft;
    protected String model;
    protected String prefix;
    protected Integer transactionCount;
    protected Double transactionDebit;
    @Temporal(value = TemporalType.DATE)
    protected Date transactionDate;
    protected Boolean isContract = false;
    @Enumerated(value = EnumType.ORDINAL)
    protected MachineState state = MachineState.HAS_ERROR;

}
