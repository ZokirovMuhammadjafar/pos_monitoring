package com.pos.monitoring.services;

import com.pos.monitoring.dtos.pageable.TransactionCalculatePageableSearch;
import com.pos.monitoring.entities.TransactionCalculate;
import org.springframework.data.domain.Page;

public interface TransactionCalculateService {

    Page<TransactionCalculate> getAll(TransactionCalculatePageableSearch pageableSearch);
}
