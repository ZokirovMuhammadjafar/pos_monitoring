package com.pos.monitoring.entities;

import com.pos.monitoring.entities.enums.MachineState;
import com.pos.monitoring.entities.enums.Soft;
import com.pos.monitoring.entities.enums.SynchronizeType;
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
    protected String merchantName;
    protected String mcc;
    protected Boolean isContract = false;//biz bilan kontract imzolaganmi yoqmi shuni aniqlash
    protected String status;//7003 status
    @Enumerated(value = EnumType.ORDINAL)
    protected MachineState state = MachineState.HAS_ERROR;


    protected Boolean syncedTransaction =false;
    protected Integer transactionCount;
    protected Double transactionDebit;
    protected Date transactionDate;
    protected Integer dailyTransactionLevel=0;//transaction levellarga bo'linganda ishlatilgan
    @Enumerated(value = EnumType.STRING)
    protected SynchronizeType synchronizationType=SynchronizeType.TYPICAL;
}
