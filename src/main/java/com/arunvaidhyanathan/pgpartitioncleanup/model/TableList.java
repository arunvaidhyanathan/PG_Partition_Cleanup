package com.arunvaidhyanathan.pgpartitioncleanup.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TABLE_LIST")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableList {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String tableName;
    private String tableSchema;
    private boolean hasPartitions;
    
    // Constructor without id for easier creation
    public TableList(String tableName, String tableSchema, boolean hasPartitions) {
        this.tableName = tableName;
        this.tableSchema = tableSchema;
        this.hasPartitions = hasPartitions;
    }
}