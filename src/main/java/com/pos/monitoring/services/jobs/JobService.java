package com.pos.monitoring.services.jobs;

import com.pos.monitoring.services.BranchService;
import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.services.PlumService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile(value = "prod")
public class JobService {

    private final BranchService branchService;
    private final MachineService machineService;
    private final PlumService plumService;

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

    //    @Scheduled(fixedDelay = 60000)
    @Scheduled(cron = "0 0 1 * * *")
    public void synchronizePosTerminalDailyTransaction() {
        System.out.println("------------ Pos Transaction start synchronization------------");

        plumService.dailySynchronizeAmount();

        System.out.println("------------ Pos Transaction end synchronization------------");
    }
}
