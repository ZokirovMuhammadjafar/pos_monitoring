package com.pos.monitoring.services.impl;

import com.pos.monitoring.repositories.MachineRepository;
import com.pos.monitoring.services.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private final MachineRepository machineRepository;

    @Override
    public void synchronize() {

    }
}
