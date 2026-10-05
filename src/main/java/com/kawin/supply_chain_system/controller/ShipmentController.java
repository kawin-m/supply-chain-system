package com.kawin.supply_chain_system.controller;

import com.kawin.supply_chain_system.entity.Shipment;
import com.kawin.supply_chain_system.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @PostMapping
    public Shipment createShipment(@RequestBody Map<String, Object> request) {
        Long salesOrderId = Long.valueOf(request.get("salesOrderId").toString());
        String carrier = (String) request.get("carrier");
        String trackingNumber = (String) request.get("trackingNumber");
        return shipmentService.createShipment(salesOrderId, carrier, trackingNumber);
    }

    @GetMapping
    public List<Shipment> getAllShipments() {
        return shipmentService.getAllShipments();
    }

    @GetMapping("/{id}")
    public Shipment getShipment(@PathVariable Long id) {
        return shipmentService.getShipmentById(id);
    }
}