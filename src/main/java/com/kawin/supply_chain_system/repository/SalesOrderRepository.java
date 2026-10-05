package com.kawin.supply_chain_system.repository;

import com.kawin.supply_chain_system.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
}