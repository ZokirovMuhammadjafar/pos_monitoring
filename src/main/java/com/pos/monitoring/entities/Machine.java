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

import java.time.LocalDate;
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
    protected String branchMfo;//buni branch bilan join operationdan qutulish uchun yozilgan

    @Column
    protected String merchantId;

    @Column
    protected String terminalId;

    @Enumerated(value = EnumType.STRING)
    protected Soft soft;   //teminal softi masalan uzpos ptp operation uchun
    protected String model;//teminal modeli masalan pax-90 pax-920 s90
    protected String merchantName;//dokonchi nomi
    protected String mcc;
    protected String status;//7003 status

    @Enumerated(value = EnumType.ORDINAL)
    protected MachineState state = MachineState.HAS_ERROR;


    protected Boolean syncedTransaction = false;
    protected Integer transactionCount;
    protected Double transactionDebit;

    @Column(columnDefinition = "bool default false")
    protected boolean activeTransaction = false;

    protected Boolean isContract = false;//biz bilan kontract imzolaganmi yoqmi shuni aniqlash

}
