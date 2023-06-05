package com.pos.monitoring.services.jobs;

import com.pos.monitoring.services.BranchService;
import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.services.PlumService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Profile(value = "dev")
@Service
@RequiredArgsConstructor
public class JobService {

    private final BranchService branchService;
    private final MachineService machineService;
    private final PlumService plumService;

    //        @Scheduled(cron = "0 * * * * *")
    public void synchronizeBranch() {
        branchService.synchronize();
    }

    //            @Scheduled(fixedDelay = 10000)
    @Scheduled(fixedRate = 600000)
    public void synchronizeDailyTransactionCount() {
        plumService.getDailyTransactionInfo();
    }

    @Scheduled(cron = "0 0 18 * * *")
    public void synchronizeMachine() throws InterruptedException {
        for (int i = 0; i < 600_000; i = i + 100) {
            machineService.synchronize(i);
            System.out.println(i);
        }
    }
}
