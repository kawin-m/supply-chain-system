package com.kawin.supply_chain_system.service;

import com.kawin.supply_chain_system.entity.Inventory;
import com.kawin.supply_chain_system.entity.Product;
import com.kawin.supply_chain_system.entity.Warehouse;
import com.kawin.supply_chain_system.repository.InventoryRepository;
import com.kawin.supply_chain_system.repository.ProductRepository;
import com.kawin.supply_chain_system.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    public Inventory createInventory(Inventory inventory) {
        if (inventory.getProduct() == null || inventory.getProduct().getId() == null
                || inventory.getWarehouse() == null || inventory.getWarehouse().getId() == null) {
            throw new RuntimeException("Both product id and warehouse id are required");
        }

        Long productId = inventory.getProduct().getId();
        Long warehouseId = inventory.getWarehouse().getId();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + warehouseId));

        if (inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId).isPresent()) {
            throw new RuntimeException("Inventory already exists for this product in this warehouse");
        }

        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        return inventoryRepository.save(inventory);
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
    }
}