package com.pos.monitoring.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TerminalModelCreateDto implements Serializable {

    private String name;
    private String prefix;
    private Boolean valid;
}
