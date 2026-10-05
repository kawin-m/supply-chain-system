package com.kawin.supply_chain_system.controller;

import com.kawin.supply_chain_system.entity.SalesOrder;
import com.kawin.supply_chain_system.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales-orders")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping
    public SalesOrder createSalesOrder(@RequestBody SalesOrder order) {
        return salesOrderService.createSalesOrder(order);
    }

    @GetMapping
    public List<SalesOrder> getAllSalesOrders() {
        return salesOrderService.getAllSalesOrders();
    }

    @GetMapping("/{id}")
    public SalesOrder getSalesOrder(@PathVariable Long id) {
        return salesOrderService.getSalesOrderById(id);
    }
}