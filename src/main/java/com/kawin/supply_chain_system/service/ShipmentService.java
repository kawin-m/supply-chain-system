package com.kawin.supply_chain_system.service;

import com.kawin.supply_chain_system.entity.*;
import com.kawin.supply_chain_system.repository.InventoryRepository;
import com.kawin.supply_chain_system.repository.SalesOrderRepository;
import com.kawin.supply_chain_system.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Transactional
    public Shipment createShipment(Long salesOrderId, String carrier, String trackingNumber) {
        SalesOrder order = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new RuntimeException("Sales order not found with id: " + salesOrderId));

        if (order.getStatus() != SalesOrderStatus.PENDING) {
            throw new RuntimeException("Only PENDING sales orders can be shipped. Current status: " + order.getStatus());
        }

        Long warehouseId = order.getWarehouse().getId();

        for (SalesOrderItem item : order.getItems()) {
            Long productId = item.getProduct().getId();

            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouseId(productId, warehouseId)
                    .orElseThrow(() -> new RuntimeException(
                            "No inventory for product " + productId + " in warehouse " + warehouseId));

            inventory.setQuantityOnHand(inventory.getQuantityOnHand() - item.getQuantity());
            inventory.setQuantityReserved(inventory.getQuantityReserved() - item.getQuantity());
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);
        }

        order.setStatus(SalesOrderStatus.SHIPPED);
        salesOrderRepository.save(order);

        Shipment shipment = new Shipment();
        shipment.setSalesOrder(order);
        shipment.setCarrier(carrier);
        shipment.setTrackingNumber(trackingNumber);
        shipment.setStatus(ShipmentStatus.PENDING);
        return shipmentRepository.save(shipment);
    }

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public Shipment getShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + id));
    }
}