package com.pos.monitoring.services.jobs;

import com.pos.monitoring.entities.enums.SynchronizeType;
import com.pos.monitoring.repositories.TransactionInfoRepository;
import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.services.PlumService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

//@Profile(value = "dev")
@Profile(value = "prod")
@Service
@RequiredArgsConstructor
public class JobService {
    private final MachineService machineService;
    private final PlumService plumService;
    private final TransactionInfoRepository transactionInfoRepository;
    Logger logger = LogManager.getLogger(JobService.class);

    private static boolean suspend=false;
    public static boolean isBackgroundServiceWork(){
        return suspend;
    }

    public static void dontWorkBackgroundService(){
        suspend=true;
    }

    public static void workBackgroundService(){
        suspend=false;
    }



    /**
     * Accept {}
     * Run every 10 minute,
     * This job get all transaction information from Plum Tech
     */
//    @Scheduled(fixedRate = 600000)
    public void synchronizeDailyTransactionTypicalCount() {
        logger.info("------------ Transaction typical count start synchronization------------");

        plumService.getDailyTransaction(SynchronizeType.TYPICAL);

        logger.info("------------ Transaction typical count end synchronization------------");
    }

    /**
     * Accept {}
     * Run every 10 minute,
     * This job get all transaction Greater Million information from Plum Tech
     */
//    @Scheduled(fixedRate = 600000)
    public void synchronizeDailyTransactionGreaterMillionCount() {
        logger.info("------------ Transaction GREATER_THEN_MILLION count start synchronization------------");

        plumService.getDailyTransaction(SynchronizeType.GREATER_THEN_MILLION);

        logger.info("------------ Transaction GREATER_THEN_MILLION count end synchronization------------");
    }

    /**
     * Accept {}
     * Run every 10 minute,
     * This job get all transaction information from Plum Tech between 1 000 000 and 100 000
     */
//    @Scheduled(fixedRate = 600000)
    public void synchronizeDailyTransactionBetweenMillionAndHundredThousand() {
        logger.info("------------ Transaction BETWEEN_HUNDRED_THOUSAND_AND_MILLION count start synchronization------------");

        plumService.getDailyTransaction(SynchronizeType.BETWEEN_HUNDRED_THOUSAND_AND_MILLION);

        logger.info("------------ Transaction BETWEEN_HUNDRED_THOUSAND_AND_MILLION count end synchronization------------");
    }

    /**
     * Accept {}
     * Run every 10 minute,
     * This job get all transaction information from Plum Tech Lower than 100 000
     */
//    @Scheduled(fixedRate = 600000)
    public void synchronizeDailyTransactionLowerThanHundredThousand() {
        logger.info("------------ Transaction LOWER_THAN_HUNDRED_THOUSAND count start synchronization------------");

        plumService.getDailyTransaction(SynchronizeType.LOWER_THAN_HUNDRED_THOUSAND);

        logger.info("------------ Transaction LOWER_THAN_HUNDRED_THOUSAND count end synchronization------------");
    }


    /**
     * Accept {}
     * Run every 10 minute,
     * This job get all transaction information from Plum Tech
     */
//    @Scheduled(fixedRate = 600000)
    public void synchronizeDailyTransactionCassieCount() {
        logger.info("------------ Transaction cassie count start synchronization------------");

        plumService.getDailyTransaction(SynchronizeType.KASSA);

        logger.info("------------ Transaction cassie count end synchronization------------");
    }

//    @Scheduled(cron = "0 50 23 * * *")
    public void syncedFalseDailyTransaction() throws InterruptedException {
        logger.info("--------------------------begin sync false---------------------");
        dontWorkBackgroundService();
        Thread.sleep(300000);
        machineService.synchronizeTransactionFalse();
        logger.info("--------------------------end sync false---------------------");
    }

//    @Scheduled(cron = "0 57 23 * * *")
    public void changeStatus() throws InterruptedException {
        logger.info("------------------- change status begin using transactions-------------------");
        dontWorkBackgroundService();
        Thread.sleep(600000);
        long begin=System.currentTimeMillis();
        HashMap<String,Double>amountTransaction=new HashMap<>();
       transactionInfoRepository.getAllMax().forEach(a->{
           amountTransaction.put(""+a.get("terminal_merchant"),(double) a.get("amount"));
       });
        machineService.changesynchronizeType(PageRequest.of(0,1000),amountTransaction);
        logger.info("------------------- change status end {} using transactions-------------------",System.currentTimeMillis()-begin);
        workBackgroundService();
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
