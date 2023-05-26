package com.pos.monitoring.controller;

import com.pos.monitoring.dtos.pageable.MachineHistoryPageableSearch;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.entities.MachineHistory;
import com.pos.monitoring.services.MachineHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/terminal-histories")
@RequiredArgsConstructor
@CrossOrigin
public class MachineHistoryController {

    private final MachineHistoryService machineHistoryService;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @PostMapping(value = "/get-all", produces = "application/json")
    public ListResponse getAll(@RequestBody MachineHistoryPageableSearch pageableSearch) {
        Page<MachineHistory> pageable = machineHistoryService.getAll(pageableSearch);
        return ListResponse.of(pageable, MachineHistory.class);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @GetMapping("/{id}")
    public SingleResponse getById(@PathVariable Long id) {
        return SingleResponse.of(machineHistoryService.get(id));
    }
}
