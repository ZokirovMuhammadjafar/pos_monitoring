package com.pos.monitoring.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "machines")
public class Machine extends AbstractEntity {
    @Column
    protected String srNumber;
    @Column
    protected String instId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
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
    protected Boolean isContract = false;

    @Enumerated(value = EnumType.ORDINAL)
    protected MachineState state = MachineState.HAS_ERROR;

    @Override
    public String toString() {
        return "Machine{" + "srNumber='" + srNumber + '\'' + ", instId='" + instId + '\'' + ", branchMfo='" + branchMfo + '\'' + ", merchantId='" + merchantId + '\'' + ", terminalId='" + terminalId + '\'' + ", prefix='" + prefix + '\'' + ", isContract=" + isContract + ", state=" + state + '}';
    }
}
