package com.pos.monitoring.entities;

import com.pos.monitoring.dtos.enums.MachineHistoryState;
import jakarta.persistence.*;
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
@Table(name = "machine_history")
public class MachineHistory extends AbstractEntity {
    @Column
    protected String srNumber;
    @Column
    protected String toInstId;
    @Column
    protected String fromInstId;
    @Enumerated(EnumType.ORDINAL)
    protected MachineHistoryState state;
}
