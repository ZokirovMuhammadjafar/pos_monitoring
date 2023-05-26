package com.pos.monitoring.services.jobs;

import com.pos.monitoring.services.BranchService;
import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.services.PlumService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Profile(value = "prod")
@Service
@RequiredArgsConstructor
public class JobService {

    private final BranchService branchService;
    private final MachineService machineService;
    private final PlumService plumService;

    //    @Scheduled(cron = "0 * * * * *")
    public void synchronizeBranch() {
        System.out.println("------------ Branches start synchronization------------");
        branchService.synchronize();
        System.out.println("------------ Branches end synchronization------------");
    }

    //    @Scheduled(fixedDelay = 10000)
    @Scheduled(cron = "0 0 1 * * *")
    public void synchronizeDailyTransactionCount() {
        System.out.println("------------ Transaction count start synchronization------------");

        plumService.getDailyTransactionCount();

        System.out.println("------------ Transaction count end synchronization------------");
    }

    @Scheduled(cron = "0 10 18 * * *")
    public void synchronizeMachine() throws InterruptedException {
        System.out.println("------------ Machines start synchronization------------");
        for (int i = 0; i < 600_000; i = i + 100) {
            machineService.synchronize(i);
            System.out.println(i);
        }

        System.out.println("------------ Machines end synchronization------------");
    }
}
