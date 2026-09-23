package com.kawin.supply_chain_system.repository;

import com.kawin.supply_chain_system.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}