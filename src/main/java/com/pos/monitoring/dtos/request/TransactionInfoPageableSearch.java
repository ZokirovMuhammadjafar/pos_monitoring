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
public class TransactionInfoPageableSearch extends PageableSearch {

    private String mfo;
    private String parentMfo;
}
