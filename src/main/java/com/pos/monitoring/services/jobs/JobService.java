package com.pos.monitoring.services.jobs;

import com.pos.monitoring.services.BranchService;
import com.pos.monitoring.services.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {

    private final BranchService branchService;
    private final MachineService machineService;

    @Scheduled(cron = "0 * * * * *")
    public void synchronizeBranch() {
        System.out.println("------------ Branches start synchronization------------");
        branchService.synchronize();
        System.out.println("------------ Branches end synchronization------------");
    }

    @Scheduled(cron = "0 * * * * *")
    public void synchronizeMachine() {
        System.out.println("------------ Machines start synchronization------------");
        machineService.synchronize();
        System.out.println("------------ Machines end synchronization------------");
    }
}
