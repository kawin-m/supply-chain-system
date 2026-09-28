package com.kawin.supply_chain_system.controller;

import com.kawin.supply_chain_system.entity.PurchaseOrder;
import com.kawin.supply_chain_system.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public PurchaseOrder createPurchaseOrder(@RequestBody PurchaseOrder order) {
        return purchaseOrderService.createPurchaseOrder(order);
    }

    @PutMapping("/{id}/receive")
    public PurchaseOrder receivePurchaseOrder(@PathVariable Long id, @RequestParam Long warehouseId) {
        return purchaseOrderService.receivePurchaseOrder(id, warehouseId);
    }

    @GetMapping
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderService.getAllPurchaseOrders();
    }

    @GetMapping("/{id}")
    public PurchaseOrder getPurchaseOrder(@PathVariable Long id) {
        return purchaseOrderService.getPurchaseOrderById(id);
    }
}