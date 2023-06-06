package com.pos.monitoring.services.jobs;

import com.pos.monitoring.services.BranchService;
import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.services.PlumService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    Logger logger = LogManager.getLogger(JobService.class);

    public void synchronizeBranch() {
        logger.info("------------ Branches start synchronization------------");
        branchService.synchronize();
        logger.info("------------ Branches end synchronization------------");
    }

    /**
     * Accept {}
     * Run every 10 minute,
     * This job get all transaction information from Plum Tech
     */
    @Scheduled(fixedRate = 600000)
    public void synchronizeDailyTransactionCount() {
        logger.info("------------ Transaction count start synchronization------------");

        plumService.getDailyTransactionInfo();

        logger.info("------------ Transaction count end synchronization------------");
    }

    /**
     * Accept {}
     * Run every 20 minute
     * This job calculate transaction and count which is taken from Plum Tech
     */
    @Scheduled(fixedRate = 1200000)
    public void calculateTransactionAndCount() {
        logger.info("------------ Calculate transaction and count start calculate ------------");

        plumService.calculateTransactionAndCount();

        logger.info("------------ Calculate transaction and count end calculate------------");
    }

    @Scheduled(cron = "0 0 18 * * *")
    public void synchronizeMachine() {
        System.out.println("------------ Machines start synchronization------------");
        for (int i = 0; i < 600_000; i = i + 100) {
            machineService.synchronizeDailyChanges(i);
            System.out.println(i);
        }
        System.out.println("------------ Machines end synchronization------------");
    }

    @Scheduled(cron = "0 50 23 * * *")
    public void synchronizeFix() {
        logger.info("---------------------fix begin---------------------");
        machineService.synchronizeFix();
        logger.info("---------------------fix end---------------------");
    }

    @Scheduled(cron = "0 55 23 * * *")
    public void synchronizeAuthCode() {
        logger.info("---------------------auth code begin---------------------");
        machineService.synchronizeAuthCode();
        logger.info("---------------------auth code end---------------------");
    }


}
