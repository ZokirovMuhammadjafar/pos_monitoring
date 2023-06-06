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

@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "daily_synchronizes")
public class DailySynchronize extends AbstractEntity {

    private String today;

    private Integer total;

    @Column(columnDefinition = "int default 0")
    private int cycles;

    @Column(columnDefinition = "int default 0")
    private int cycle;

    @Column(columnDefinition = "boolean default false")
    private boolean isCalculate;
}
