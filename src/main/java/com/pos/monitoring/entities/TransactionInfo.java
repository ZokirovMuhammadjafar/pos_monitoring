package com.pos.monitoring.entities;

import com.pos.monitoring.dtos.response.plum.PlumDailyTransactionInfoDto;
import com.pos.monitoring.utils.TimeUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_infos")
public class TransactionInfo extends AbstractEntity {

    @Column(nullable = false)
    protected String mfo;

    @Column(nullable = false)
    protected String terminalId;

    @Column(nullable = false)
    protected String merchantId;

    protected Double amount;

    protected int total;

    protected LocalDate transactionsDay;

    public static TransactionInfo build(Machine machine, PlumDailyTransactionInfoDto infoDto, LocalDate localDate) {
        return new TransactionInfo(
                machine.getBranchMfo(),
                machine.getTerminalId(),
                machine.getMerchantId(),
                infoDto.getTotalDebit(),
                infoDto.getTotalCount(),
                localDate
        );
    }
}
