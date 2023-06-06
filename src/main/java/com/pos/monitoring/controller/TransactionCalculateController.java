package com.pos.monitoring.controller;

import com.pos.monitoring.dtos.pageable.TransactionCalculatePageableSearch;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.entities.TransactionCalculate;
import com.pos.monitoring.services.TransactionCalculateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction-calculates")
@RequiredArgsConstructor
@CrossOrigin
public class TransactionCalculateController {

    private final TransactionCalculateService transactionCalculateService;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @PostMapping(value = "/get-all", produces = "application/json")
    public ListResponse getAll(@RequestBody TransactionCalculatePageableSearch pageableSearch) {
        Page<TransactionCalculate> transactionCalculates = transactionCalculateService.getAll(pageableSearch);
        return ListResponse.of(transactionCalculates, TransactionCalculate.class);
    }
}
