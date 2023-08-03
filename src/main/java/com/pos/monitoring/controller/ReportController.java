package com.pos.monitoring.controller;

import com.pos.monitoring.dtos.response.ListResponse;
import com.pos.monitoring.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/mfo/{mfo}")
    public ListResponse reportSingleMfo( @PathVariable String mfo) {
        List<Map<String, String>> reportByMfo = reportService.getReportByMfo(mfo);
        return ListResponse.of(reportByMfo, reportByMfo.size());
    }

}
