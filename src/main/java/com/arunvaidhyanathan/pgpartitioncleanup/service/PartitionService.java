package com.arunvaidhyanathan.pgpartitioncleanup.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arunvaidhyanathan.pgpartitioncleanup.model.EmptyPartition;
import com.arunvaidhyanathan.pgpartitioncleanup.model.TableList;
import com.arunvaidhyanathan.pgpartitioncleanup.repository.EmptyPartitionRepository;
import com.arunvaidhyanathan.pgpartitioncleanup.repository.TableListRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PartitionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private TableListRepository tableListRepository;
    
    @Autowired
    private EmptyPartitionRepository emptyPartitionRepository;
    
    /**
     * Identifies all tables in the CADS schema and stores them in the TABLE_LIST table
     * @return The number of tables identified
     */
    @Transactional
    public int identifyTables() {
        log.info("Starting to identify tables in CADS schema");
        
        // Query to get all tables in the CADS schema
        String sql = "SELECT table_name FROM information_schema.tables " +
                     "WHERE table_schema = 'CADS' AND table_type = 'BASE TABLE'";
        
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        int count = 0;
        
        for (Map<String, Object> row : rows) {
            String tableName = (String) row.get("table_name");
            
            // Check if the table has partitions
            boolean hasPartitions = checkIfTableHasPartitions(tableName);
            
            // Only add if it doesn't already exist
            if (!tableListRepository.existsByTableNameAndTableSchema(tableName, "CADS")) {
                TableList tableList = new TableList(tableName, "CADS", hasPartitions);
                tableListRepository.save(tableList);
                count++;
            }
        }
        
        log.info("Identified {} tables in CADS schema", count);
        return count;
    }
    
    /**
     * Checks if a table has partitions
     * @param tableName The name of the table to check
     * @return true if the table has partitions, false otherwise
     */
    private boolean checkIfTableHasPartitions(String tableName) {
        String sql = "SELECT count(*) FROM pg_inherits i " +
                     "JOIN pg_class c ON c.oid = i.inhparent " +
                     "JOIN pg_namespace n ON n.oid = c.relnamespace " +
                     "WHERE n.nspname = 'CADS' AND c.relname = ?";
        
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
        return count != null && count > 0;
    }
    
    /**
     * Identifies all empty partitions for tables in the CADS schema and stores them in the EMPTY_PARTITIONS table
     * @return The number of empty partitions identified
     */
    @Transactional
    public int identifyEmptyPartitions() {
        log.info("Starting to identify empty partitions");
        
        // Get all tables with partitions
        List<TableList> tablesWithPartitions = tableListRepository.findByHasPartitionsTrue();
        int count = 0;
        
        for (TableList table : tablesWithPartitions) {
            // Find all partitions for this table
            String sql = "SELECT c.relname AS partition_name " +
                         "FROM pg_inherits i " +
                         "JOIN pg_class p ON p.oid = i.inhparent " +
                         "JOIN pg_class c ON c.oid = i.inhrelid " +
                         "JOIN pg_namespace n ON n.oid = p.relnamespace " +
                         "WHERE n.nspname = 'CADS' AND p.relname = ?";
            
            List<Map<String, Object>> partitions = jdbcTemplate.queryForList(sql, table.getTableName());
            
            for (Map<String, Object> partition : partitions) {
                String partitionName = (String) partition.get("partition_name");
                
                // Check if the partition is empty
                String countSql = "SELECT count(*) FROM \"CADS\".\""+partitionName+"\"";
                Integer rowCount = jdbcTemplate.queryForObject(countSql, Integer.class);
                
                if (rowCount != null && rowCount == 0) {
                    // Only add if it doesn't already exist
                    if (!emptyPartitionRepository.existsByTableNameAndPartitionName(table.getTableName(), partitionName)) {
                        EmptyPartition emptyPartition = new EmptyPartition(table.getTableName(), partitionName);
                        emptyPartitionRepository.save(emptyPartition);
                        count++;
                    }
                }
            }
        }
        
        log.info("Identified {} empty partitions", count);
        return count;
    }
    
    /**
     * Drops all empty partitions that have not been dropped yet
     * @return The number of partitions dropped
     */
    @Transactional
    public int dropEmptyPartitions() {
        log.info("Starting to drop empty partitions");
        
        List<EmptyPartition> emptyPartitions = emptyPartitionRepository.findByIsDroppedFalse();
        int count = 0;
        
        for (EmptyPartition partition : emptyPartitions) {
            try {
                // Drop the partition
                String sql = "ALTER TABLE \"CADS\".\""+partition.getTableName()+"\" DETACH PARTITION \"CADS\".\""+partition.getPartitionName()+"\"";
                jdbcTemplate.execute(sql);
                
                // Drop the detached table
                String dropSql = "DROP TABLE \"CADS\".\""+partition.getPartitionName()+"\"";
                jdbcTemplate.execute(dropSql);
                
                // Update the record
                partition.setDropped(true);
                partition.setDroppedAt(LocalDateTime.now());
                emptyPartitionRepository.save(partition);
                
                count++;
                log.info("Successfully dropped partition: {}", partition.getPartitionName());
            } catch (Exception e) {
                log.error("Error dropping partition: {}", partition.getPartitionName(), e);
            }
        }
        
        log.info("Dropped {} empty partitions", count);
        return count;
    }
    
    /**
     * Gets all empty partitions that have not been dropped yet
     * @return List of empty partitions
     */
    public List<EmptyPartition> getEmptyPartitions() {
        return emptyPartitionRepository.findByIsDroppedFalse();
    }
}