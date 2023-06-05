package com.pos.monitoring.entities;

import com.pos.monitoring.entities.enums.DailyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "daily_terminal_info")
@ToString
public class DailyTerminalInfo extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id")
    private Machine machine;
    private DailyStatus status;
    private String billingType;
    private String applicationNumber;
    @Transient
    private String srNumber;
}
