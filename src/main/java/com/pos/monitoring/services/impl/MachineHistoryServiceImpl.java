package com.pos.monitoring.services.impl;

import com.pos.monitoring.repositories.MachineHistoryRepository;
import com.pos.monitoring.services.MachineHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MachineHistoryServiceImpl implements MachineHistoryService {
    private final MachineHistoryRepository machineHistoryRepository;
}
