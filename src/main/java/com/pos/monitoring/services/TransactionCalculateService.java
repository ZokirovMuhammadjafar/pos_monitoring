package com.pos.monitoring.services;

import com.pos.monitoring.dtos.pageable.TransactionCalculatePageableSearch;
import com.pos.monitoring.entities.TransactionCalculate;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TransactionCalculateService {

    List<TransactionCalculate> getAll(TransactionCalculatePageableSearch pageableSearch);
}
