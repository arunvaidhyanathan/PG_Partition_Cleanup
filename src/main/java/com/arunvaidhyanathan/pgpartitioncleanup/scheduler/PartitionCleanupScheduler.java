package com.arunvaidhyanathan.pgpartitioncleanup.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.arunvaidhyanathan.pgpartitioncleanup.service.PartitionService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@Slf4j
public class PartitionCleanupScheduler {

    @Autowired
    private PartitionService partitionService;
    
    /**
     * Scheduled task to identify tables and empty partitions
     * Runs every day at 1:00 AM
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void schedulePartitionIdentification() {
        log.info("Starting scheduled partition identification");
        try {
            int tablesIdentified = partitionService.identifyTables();
            log.info("Identified {} tables", tablesIdentified);
            
            int emptyPartitionsIdentified = partitionService.identifyEmptyPartitions();
            log.info("Identified {} empty partitions", emptyPartitionsIdentified);
        } catch (Exception e) {
            log.error("Error during scheduled partition identification", e);
        }
    }
    
    /**
     * Scheduled task to drop empty partitions
     * Runs every Sunday at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 ? * SUN")
    public void schedulePartitionCleanup() {
        log.info("Starting scheduled partition cleanup");
        try {
            int partitionsDropped = partitionService.dropEmptyPartitions();
            log.info("Dropped {} empty partitions", partitionsDropped);
        } catch (Exception e) {
            log.error("Error during scheduled partition cleanup", e);
        }
    }
}