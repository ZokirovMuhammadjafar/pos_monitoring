package com.pos.monitoring.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Map;

@Entity
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "terminal_model")
public class TerminalModel extends AbstractEntity {
    private String name;

    private String prefix;
    private Boolean valid=Boolean.FALSE;
}
