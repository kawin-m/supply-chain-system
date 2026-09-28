package com.kawin.supply_chain_system.repository;

import com.kawin.supply_chain_system.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
}