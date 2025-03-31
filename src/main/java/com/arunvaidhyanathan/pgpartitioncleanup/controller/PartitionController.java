package com.arunvaidhyanathan.pgpartitioncleanup.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arunvaidhyanathan.pgpartitioncleanup.model.EmptyPartition;
import com.arunvaidhyanathan.pgpartitioncleanup.service.PartitionService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/partitions")
@Slf4j
public class PartitionController {

    @Autowired
    private PartitionService partitionService;
    
    /**
     * Endpoint to identify all tables in the CADS schema
     * @return ResponseEntity with the number of tables identified
     */
    @PostMapping("/identify-tables")
    public ResponseEntity<Map<String, Object>> identifyTables() {
        log.info("Received request to identify tables");
        int count = partitionService.identifyTables();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Tables identification completed");
        response.put("tablesIdentified", count);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint to identify all empty partitions in the CADS schema
     * @return ResponseEntity with the number of empty partitions identified
     */
    @PostMapping("/identify-empty-partitions")
    public ResponseEntity<Map<String, Object>> identifyEmptyPartitions() {
        log.info("Received request to identify empty partitions");
        int count = partitionService.identifyEmptyPartitions();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Empty partitions identification completed");
        response.put("emptyPartitionsIdentified", count);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint to drop all empty partitions that have not been dropped yet
     * @return ResponseEntity with the number of partitions dropped
     */
    @PostMapping("/drop-empty-partitions")
    public ResponseEntity<Map<String, Object>> dropEmptyPartitions() {
        log.info("Received request to drop empty partitions");
        int count = partitionService.dropEmptyPartitions();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Empty partitions drop completed");
        response.put("partitionsDropped", count);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint to get all empty partitions that have not been dropped yet
     * @return ResponseEntity with the list of empty partitions
     */
    @GetMapping("/empty-partitions")
    public ResponseEntity<List<EmptyPartition>> getEmptyPartitions() {
        log.info("Received request to get empty partitions");
        List<EmptyPartition> emptyPartitions = partitionService.getEmptyPartitions();
        
        return ResponseEntity.ok(emptyPartitions);
    }
}