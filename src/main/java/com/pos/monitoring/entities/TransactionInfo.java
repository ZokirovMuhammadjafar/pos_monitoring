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

    @Column(nullable = false)
    protected String transactionDate;

    @Column(nullable = false)
    protected String today;

    public static TransactionInfo build(Machine machine, PlumDailyTransactionInfoDto infoDto, String todayAsString, Date yesterday) {
        return new TransactionInfo(machine.getBranchMfo(), machine.getTerminalId(),
                machine.getMerchantId(), infoDto.getTotalDebit(), infoDto.getTotalCount(), TimeUtils.toYYYYmmDD(yesterday), todayAsString);
    }
}
