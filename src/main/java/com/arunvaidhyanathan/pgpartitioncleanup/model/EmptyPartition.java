package com.arunvaidhyanathan.pgpartitioncleanup.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "EMPTY_PARTITIONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmptyPartition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String tableName;
    private String partitionName;
    private LocalDateTime identifiedAt;
    private boolean isDropped;
    private LocalDateTime droppedAt;
    
    // Constructor without id for easier creation
    public EmptyPartition(String tableName, String partitionName) {
        this.tableName = tableName;
        this.partitionName = partitionName;
        this.identifiedAt = LocalDateTime.now();
        this.isDropped = false;
        this.droppedAt = null;
    }
}