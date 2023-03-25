package com.pos.monitoring.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "terminal_model")
public class TerminalModel extends AbstractEntity {
    private String name;

    @Column(unique = true, nullable = false)
    private String prefix;
    private Boolean valid = Boolean.FALSE;
}
