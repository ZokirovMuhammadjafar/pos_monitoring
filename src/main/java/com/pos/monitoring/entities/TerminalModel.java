package com.pos.monitoring.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "terminal_model")
public class TerminalModel extends AbstractEntity {
    private String name;

    @Column(unique = true, nullable = false)
    private String prefix;
    private Boolean valid = Boolean.FALSE;
}
