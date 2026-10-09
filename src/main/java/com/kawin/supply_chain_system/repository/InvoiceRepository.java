package com.kawin.supply_chain_system.repository;

import com.kawin.supply_chain_system.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}