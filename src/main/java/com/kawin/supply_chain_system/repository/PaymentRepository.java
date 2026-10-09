package com.kawin.supply_chain_system.repository;

import com.kawin.supply_chain_system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoiceId(Long invoiceId);
}