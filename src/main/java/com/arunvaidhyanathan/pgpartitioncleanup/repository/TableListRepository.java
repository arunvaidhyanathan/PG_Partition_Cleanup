package com.arunvaidhyanathan.pgpartitioncleanup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arunvaidhyanathan.pgpartitioncleanup.model.TableList;

@Repository
public interface TableListRepository extends JpaRepository<TableList, Long> {
    
    List<TableList> findByHasPartitionsTrue();
    
    boolean existsByTableNameAndTableSchema(String tableName, String tableSchema);
    
}