package com.pos.monitoring.services.jobs;

import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.services.PlumService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

//@Profile(value = "dev")
@Profile(value = "prod")
@Service
@RequiredArgsConstructor
public class JobService {
    private final MachineService machineService;
    private final PlumService plumService;
    Logger logger = LogManager.getLogger(JobService.class);

    /**
     * Accept {}
     * Run every 10 minute,
     * This job get all transaction information from Plum Tech
     */
    @Scheduled(fixedRate = 600000)
    public void synchronizeDailyTransactionTypicalCount() {
        logger.info("------------ Transaction typical count start synchronization------------");

        plumService.getDailyTransactionInfo();

        logger.info("------------ Transaction typical count end synchronization------------");
    }

    /**
     * Accept {}
     * Run every 10 minute,
     * This job get all transaction information from Plum Tech
     */
    @Scheduled(fixedRate = 600000)
    public void synchronizeDailyTransactionCassieCount() {
        logger.info("------------ Transaction cassie count start synchronization------------");

        plumService.getDailyTransactionInfoCassiaTerminals();

        logger.info("------------ Transaction cassie count end synchronization------------");
    }

    /**
     * Accept {}
     * Run every 20 minute
     * This job calculate transaction and count which is taken from Plum Tech
     */
//    @Scheduled(fixedRate = 600000)
//    public void calculateTransactionAndCount() {
//        logger.info("------------ Calculate transaction and count start calculate ------------");
//
//        plumService.calculateTransactionAndCount();
//
//        logger.info("------------ Calculate transaction and count end calculate------------");
//    }

    /**
     * this method take all terminals changed without 9006 9004 9002
     */
    @Scheduled(cron = "0 0 18 * * *")
    public void synchronizeMachine() {
        logger.info("------------ Machines start synchronization------------");
        for (int i = 0; i < 600_000; i = i + 100) {
            machineService.synchronizeDailyChanges(i);
            System.out.println(i);
        }
        logger.info("------------ Machines end synchronization------------");
    }

    /**
     * this method take all terminals changed with 9006 9004 9002
     */
    @Scheduled(cron = "0 0 21 * * *")
    public void synchronizeMachineWithBanksChosen() {
        logger.info("------------ Machines start 9006 9004 9002 synchronization------------");
        for (int i = 0; i < 600_000; i = i + 100) {
            machineService.synchronizeDailyChangesWithBanksChosen(i);
            System.out.println(i);
        }
        logger.info("------------ Machines end 9006 9004 9002 synchronization------------");
    }


}
