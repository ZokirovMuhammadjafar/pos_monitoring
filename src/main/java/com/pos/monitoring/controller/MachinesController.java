package com.pos.monitoring.controller;

import com.pos.monitoring.dtos.pageable.MachineFilterDto;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.services.MachineService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/all-by-mfo")
    public ListResponse getByMfoByInstId(MachineFilterDto filterDto) {
        ListResponse listResponse = machineService.getInformationByInstId(filterDto);
        return listResponse;
    }

}
