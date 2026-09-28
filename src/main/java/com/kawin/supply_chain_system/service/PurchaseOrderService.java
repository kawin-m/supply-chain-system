package com.kawin.supply_chain_system.service;

import com.kawin.supply_chain_system.entity.*;
import com.kawin.supply_chain_system.repository.ProductRepository;
import com.kawin.supply_chain_system.repository.PurchaseOrderRepository;
import com.kawin.supply_chain_system.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder order) {
        if (order.getSupplier() == null || order.getSupplier().getId() == null) {
            throw new RuntimeException("Supplier id is required");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("A purchase order needs at least one item");
        }

        Long supplierId = order.getSupplier().getId();
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierId));

        double total = 0.0;
        for (PurchaseOrderItem item : order.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                throw new RuntimeException("Each item needs a product id");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new RuntimeException("Item quantity must be greater than 0");
            }
            if (item.getUnitCost() == null || item.getUnitCost() < 0) {
                throw new RuntimeException("Item unit cost must be 0 or more");
            }

            Long productId = item.getProduct().getId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            item.setProduct(product);
            item.setPurchaseOrder(order);
            total += item.getQuantity() * item.getUnitCost();
        }

        order.setSupplier(supplier);
        order.setStatus(PurchaseOrderStatus.PENDING);
        order.setTotalAmount(total);
        return purchaseOrderRepository.save(order);
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + id));
    }
}