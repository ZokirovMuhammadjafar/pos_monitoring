package com.pos.monitoring.services.impl;

import com.pos.monitoring.exceptions.ValidatorException;
import com.pos.monitoring.repositories.MachineRepository;
import com.pos.monitoring.repositories.system.Connection8005;
import com.pos.monitoring.services.ReportService;
import com.pos.monitoring.utils.ReflectionUtils;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final MachineRepository machineRepository;
    private final Connection8005 connection8005;

    @Override
    public List<Map<String, String>> getReportByMfo(String mfo) {
        if (StringUtils.isBlank(mfo)) {
            throw new ValidatorException("mfo don't come");
        }
        List<Map<String, Object>> reportPOS = machineRepository.report(mfo);
        List<Map<String, Object>> report7005 = null;
        try {
            report7005 = connection8005.getReportSingleMFO(mfo);
        } catch (Exception e) {
            throw new ValidatorException("7005 da xatolik");
        }
        List<Map<String, String>> report7005Map = ReflectionUtils.maptoMapString(report7005);
        List<Map<String, String>> reportPosMap = ReflectionUtils.maptoMapString(reportPOS);
        List<Map<String, String>> response = new ArrayList<>();
        boolean find = false;
        for (Map<String, String> stringStringMap : reportPosMap) {
            for (Map<String, String> stringMap : report7005Map) {
                if (stringMap.get("sr_number").equals(stringStringMap.get("sr_number"))) {
                    stringStringMap.putAll(stringMap);
                    response.add(stringStringMap);
                    find = true;
                }
            }
            if (!find) {
                response.add(stringStringMap);
            }
            find=false;
        }

        return response;
    }
}
