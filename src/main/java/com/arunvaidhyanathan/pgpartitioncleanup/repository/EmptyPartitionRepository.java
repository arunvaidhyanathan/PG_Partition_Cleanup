package com.arunvaidhyanathan.pgpartitioncleanup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arunvaidhyanathan.pgpartitioncleanup.model.EmptyPartition;

@Repository
public interface EmptyPartitionRepository extends JpaRepository<EmptyPartition, Long> {
    
    List<EmptyPartition> findByIsDroppedFalse();
    
    boolean existsByTableNameAndPartitionName(String tableName, String partitionName);
    
}