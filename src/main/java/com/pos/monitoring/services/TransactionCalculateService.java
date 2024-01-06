package com.pos.monitoring.services;


import com.pos.monitoring.dtos.request.TransactionCalculatePageableSearch;
import com.pos.monitoring.dtos.response.TransactionCalculateDTO;

import java.util.List;

public interface TransactionCalculateService {

    List<TransactionCalculateDTO> getAll(TransactionCalculatePageableSearch pageableSearch);
}
