package com.pos.monitoring.dtos.pageable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionPageableSearch extends PageableSearch {

    private String mfo;
    private String parentMfo;
}
