package com.pos.monitoring.dtos.pageable;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MachineFilterDto extends PageableSearch {
    private String instId;
    private List<String> mfos;
}
