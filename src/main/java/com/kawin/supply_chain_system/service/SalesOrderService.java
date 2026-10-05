package com.kawin.supply_chain_system.service;

import com.kawin.supply_chain_system.entity.*;
import com.kawin.supply_chain_system.repository.CustomerRepository;
import com.kawin.supply_chain_system.repository.InventoryRepository;
import com.kawin.supply_chain_system.repository.ProductRepository;
import com.kawin.supply_chain_system.repository.SalesOrderRepository;
import com.kawin.supply_chain_system.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalesOrderService {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Transactional
    public SalesOrder createSalesOrder(SalesOrder order) {
        if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            throw new RuntimeException("Customer id is required");
        }
        if (order.getWarehouse() == null || order.getWarehouse().getId() == null) {
            throw new RuntimeException("Warehouse id is required");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("A sales order needs at least one item");
        }

        Long customerId = order.getCustomer().getId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        Long warehouseId = order.getWarehouse().getId();
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + warehouseId));

        double total = 0.0;
        for (SalesOrderItem item : order.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                throw new RuntimeException("Each item needs a product id");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new RuntimeException("Item quantity must be greater than 0");
            }

            Long productId = item.getProduct().getId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouseId(productId, warehouseId)
                    .orElseThrow(() -> new RuntimeException(
                            "No inventory for product " + productId + " in warehouse " + warehouseId));

            int available = inventory.getQuantityOnHand() - inventory.getQuantityReserved();
            if (available < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product " + productId
                        + ". Available: " + available + ", requested: " + item.getQuantity());
            }

            inventory.setQuantityReserved(inventory.getQuantityReserved() + item.getQuantity());
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);

            item.setProduct(product);
            item.setUnitPrice(product.getPrice());
            item.setSalesOrder(order);
            total += item.getQuantity() * product.getPrice();
        }

        order.setCustomer(customer);
        order.setWarehouse(warehouse);
        order.setStatus(SalesOrderStatus.PENDING);
        order.setTotalAmount(total);
        return salesOrderRepository.save(order);
    }

    public List<SalesOrder> getAllSalesOrders() {
        return salesOrderRepository.findAll();
    }

    public SalesOrder getSalesOrderById(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order not found with id: " + id));
    }
}