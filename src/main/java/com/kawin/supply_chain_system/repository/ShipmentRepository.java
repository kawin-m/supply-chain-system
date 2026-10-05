package com.kawin.supply_chain_system.repository;

import com.kawin.supply_chain_system.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
}