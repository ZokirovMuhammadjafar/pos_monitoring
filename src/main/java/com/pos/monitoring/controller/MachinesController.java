package com.pos.monitoring.controller;

import com.pos.monitoring.dto.ListResponse;
import com.pos.monitoring.dto.SingleResponse;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.services.MachineService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/machines")
public class MachinesController {

    private final MachineService machineService;

    @GetMapping("/all-by-instId/{instId}")
    public SingleResponse getAllStatistics(@PathVariable String instId) {
        SingleResponse all = machineService.getStat(instId);
        return all;
    }

}
