package com.kawin.supply_chain_system;

import com.kawin.supply_chain_system.entity.*;
import com.kawin.supply_chain_system.repository.InventoryRepository;
import com.kawin.supply_chain_system.repository.ProductRepository;
import com.kawin.supply_chain_system.repository.WarehouseRepository;
import com.kawin.supply_chain_system.service.SalesOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ConcurrencyTest {

    private static final long CUSTOMER_ID = 1L;
    private static final long WAREHOUSE_ID = 1L;

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Test
    void concurrentOrdersShouldNeverOversell() throws Exception {
        int stock = 10;
        int requests = 30;

        Product product = new Product();
        product.setName("Concurrency Test Item");
        product.setSku("CONC-" + System.currentTimeMillis());
        product.setPrice(10.0);
        product = productRepository.save(product);
        final Long productId = product.getId();

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setWarehouse(warehouseRepository.findById(WAREHOUSE_ID).orElseThrow());
        inventory.setQuantityOnHand(stock);
        inventoryRepository.save(inventory);

        AtomicInteger succeeded = new AtomicInteger();
        AtomicInteger outOfStock = new AtomicInteger();
        AtomicInteger lockConflicts = new AtomicInteger();
        AtomicInteger otherErrors = new AtomicInteger();

        ExecutorService pool = Executors.newFixedThreadPool(requests);
        CountDownLatch ready = new CountDownLatch(requests);
        CountDownLatch start = new CountDownLatch(1);

        for (int i = 0; i < requests; i++) {
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    salesOrderService.createSalesOrder(buildOrder(productId, 1));
                    succeeded.incrementAndGet();
                } catch (Exception e) {
                    if (hasMessage(e, "Insufficient stock")) {
                        outOfStock.incrementAndGet();
                    } else if (hasType(e, "OptimisticLock") || hasType(e, "StaleState")) {
                        lockConflicts.incrementAndGet();
                    } else {
                        otherErrors.incrementAndGet();
                        System.out.println("OTHER ERROR: " + e);
                    }
                }
            });
        }

        ready.await();
        start.countDown();
        pool.shutdown();
        pool.awaitTermination(60, TimeUnit.SECONDS);

        Inventory result = inventoryRepository
                .findByProductIdAndWarehouseId(productId, WAREHOUSE_ID).orElseThrow();

        System.out.println("=== CONCURRENCY RESULT ===");
        System.out.println("Stock on hand:        " + stock);
        System.out.println("Requests fired:       " + requests);
        System.out.println("Succeeded:            " + succeeded.get() + " (ideal: " + stock + ")");
        System.out.println("Rejected, no stock:   " + outOfStock.get());
        System.out.println("Rejected, lock clash: " + lockConflicts.get());
        System.out.println("Other errors:         " + otherErrors.get());
        System.out.println("Final reserved:       " + result.getQuantityReserved());
        System.out.println("==========================");

        assertTrue(result.getQuantityReserved() <= stock,
                "OVERSOLD: reserved " + result.getQuantityReserved() + " but only " + stock + " in stock");
        assertEquals(succeeded.get(), result.getQuantityReserved(),
                "Reserved quantity must equal the number of successful orders");
    }

    private SalesOrder buildOrder(Long productId, int quantity) {
        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(WAREHOUSE_ID);

        Product product = new Product();
        product.setId(productId);

        SalesOrderItem item = new SalesOrderItem();
        item.setProduct(product);
        item.setQuantity(quantity);

        SalesOrder order = new SalesOrder();
        order.setCustomer(customer);
        order.setWarehouse(warehouse);
        order.getItems().add(item);
        return order;
    }

    private boolean hasMessage(Throwable t, String text) {
        while (t != null) {
            if (t.getMessage() != null && t.getMessage().contains(text)) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }

    private boolean hasType(Throwable t, String nameFragment) {
        while (t != null) {
            if (t.getClass().getName().contains(nameFragment)) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
}