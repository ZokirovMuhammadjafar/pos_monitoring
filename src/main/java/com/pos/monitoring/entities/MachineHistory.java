package com.pos.monitoring.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "machine_history")
public class MachineHistory extends AbstractEntity{
    @Column
    protected String srNumber;
    @Column
    protected String toInstId;
    @Column
    protected String fromInstId;
    @Column
    protected String merchantId;
    @Column
    protected String terminalId;
    @Enumerated(EnumType.ORDINAL)
    protected State state;
}
