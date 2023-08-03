package com.pos.monitoring.services;

import java.util.List;
import java.util.Map;

public interface ReportService {

    List<Map<String,String>>getReportByMfo(String mfo);

}
