package com.pos.monitoring.services;

import com.pos.monitoring.dtos.request.TransactionInfoPageableSearch;
import com.pos.monitoring.entities.TransactionInfo;
import org.springframework.data.domain.Page;

public interface TransactionInfoService {

    Page<TransactionInfo> getAll(TransactionInfoPageableSearch pageableSearch);

    TransactionInfo get(Long id);
}
