package com.pos.monitoring.services;

import com.pos.monitoring.dtos.pageable.TerminalModelPageableSearch;
import com.pos.monitoring.dtos.pageable.TransactionPageableSearch;
import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.entities.Transaction;
import org.springframework.data.domain.Page;

public interface TransactionService {

    Page<Transaction> getAll(TransactionPageableSearch pageableSearch);

    Transaction get(Long id);
}
