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

//    @Scheduled(cron = "0 * * * * *")
    public void synchronizeBranch() {
        System.out.println("------------ Branches start synchronization------------");
        branchService.synchronize();
        System.out.println("------------ Branches end synchronization------------");
    }

    @Scheduled(cron = "0 32 9 * * *")
    public void synchronizeMachine() throws InterruptedException {
        System.out.println("------------ Machines start synchronization------------");
        for (int i=0;i<600_000;i=i+100){
            machineService.synchronize(i);
            System.out.println(i);
        }

        System.out.println("------------ Machines end synchronization------------");
    }
}
