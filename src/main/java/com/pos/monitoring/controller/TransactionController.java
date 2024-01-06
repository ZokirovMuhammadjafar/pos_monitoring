package com.pos.monitoring.controller;

import com.pos.monitoring.dtos.request.TransactionInfoPageableSearch;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.entities.TransactionInfo;
import com.pos.monitoring.services.TransactionInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction-infos")
@RequiredArgsConstructor
@CrossOrigin
public class TransactionController {

    private final TransactionInfoService transactionInfoService;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @PostMapping(value = "/get-all", produces = "application/json")
    public ListResponse getAll(@RequestBody TransactionInfoPageableSearch pageableSearch) {
        Page<TransactionInfo> pageable = transactionInfoService.getAll(pageableSearch);
        return ListResponse.of(pageable, TransactionInfo.class);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @GetMapping("/{id}")
    public SingleResponse getById(@PathVariable Long id) {
        return SingleResponse.of(transactionInfoService.get(id));
    }
}
