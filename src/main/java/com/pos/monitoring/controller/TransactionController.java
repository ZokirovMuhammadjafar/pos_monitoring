package com.pos.monitoring.controller;

import com.pos.monitoring.dtos.pageable.TransactionPageableSearch;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.entities.Transaction;
import com.pos.monitoring.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin
public class TransactionController {

    private final TransactionService transactionService;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @PostMapping(value = "/get-all", produces = "application/json")
    public ListResponse getAll(@RequestBody TransactionPageableSearch pageableSearch) {
        Page<Transaction> pageable = transactionService.getAll(pageableSearch);
        return ListResponse.of(pageable, Transaction.class);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @GetMapping("/{id}")
    public SingleResponse getById(@PathVariable Long id) {
        return SingleResponse.of(transactionService.get(id));
    }
}
