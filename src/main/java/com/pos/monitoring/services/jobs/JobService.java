package com.pos.monitoring.services.jobs;

import com.pos.monitoring.entities.enums.SynchronizeType;
import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.services.PlumService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//@Profile(value = "dev")
@Profile(value = "prod")
@Service
@RequiredArgsConstructor
public class JobService {
    private final MachineService machineService;
    private final PlumService plumService;
    public static List<String> mfos = Arrays.asList(
            "00325" //surhandarya termiz
    );
    Logger logger = LogManager.getLogger(JobService.class);

    @Scheduled(fixedRate = 60000)
    public void synchronizeDailyTransactionWithMfo() {
        logger.info("------------ Transaction mfo count start synchronization------------");
        plumService.getDailyTransaction(mfos);
        logger.info("------------ Transaction mfo count end synchronization------------");
    }

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

    //o'ylab korish kerak 12 gacha update qilomasa nima bolarkan deb hozir ulguradi
    @Scheduled(cron = "0 15 0 * * *")
    public void updateMachineAllTransactionStatus() {
        logger.info("---------------------begin update transactionStatus-------------------------------");
        machineService.updateAllMachineTransactionStatus(mfos);
        logger.info("---------------------end update transactionStatus-------------------------------");
    }
}
