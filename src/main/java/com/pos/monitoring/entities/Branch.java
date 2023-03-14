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
@Table(name = "branches")
public class Branch extends AbstractEntity {

    @Column
    protected   String name;

    @Column(unique = true)
    protected String mfo;

    @Column(name = "region_code")
    protected Long regionCode;

    @Column(name = "district_code")
    protected Long districtCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected Branch parent;
}
