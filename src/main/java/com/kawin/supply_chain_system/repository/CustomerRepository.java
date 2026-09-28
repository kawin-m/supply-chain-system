package com.kawin.supply_chain_system.repository;

import com.kawin.supply_chain_system.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}