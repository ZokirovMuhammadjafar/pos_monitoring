package com.pos.monitoring.dtos.pageable;

import com.pos.monitoring.entities.enums.CalculateType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCalculatePageableSearch extends PageableSearch {

    protected List<String> mfos;

    protected CalculateType calculateType;
}
