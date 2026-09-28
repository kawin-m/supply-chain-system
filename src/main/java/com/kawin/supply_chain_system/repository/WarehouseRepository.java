package com.kawin.supply_chain_system.repository;

import com.kawin.supply_chain_system.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}