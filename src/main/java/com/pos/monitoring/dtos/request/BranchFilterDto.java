package com.pos.monitoring.dtos.request;

import com.pos.monitoring.dtos.pageable.PageableSearch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BranchFilterDto extends PageableSearch {
    private String instId;
    private Long regionCode;
}
