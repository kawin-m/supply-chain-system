package com.kawin.supply_chain_system.repository;

import com.kawin.supply_chain_system.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}