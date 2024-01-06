package com.pos.monitoring.controller;

import com.pos.monitoring.dtos.request.MachineFilterDto;
import com.pos.monitoring.dtos.request.StatisticDto;
import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.services.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/machines")
public class MachinesController {

    private final MachineService machineService;

    @GetMapping("/all-by-mfos")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public SingleResponse getAllStatistics(StatisticDto dto) {
        SingleResponse all = machineService.getStatistic(dto);
        return all;
    }

    @GetMapping("/all-by-mfo")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ListResponse getByMfoByInstId(MachineFilterDto filterDto) {
        ListResponse listResponse = machineService.getInformationByInstId(filterDto);
        return listResponse;
    }

}
