package com.kawin.supply_chain_system.service;

import com.kawin.supply_chain_system.entity.Invoice;
import com.kawin.supply_chain_system.entity.InvoiceStatus;
import com.kawin.supply_chain_system.entity.SalesOrder;
import com.kawin.supply_chain_system.entity.SalesOrderStatus;
import com.kawin.supply_chain_system.repository.InvoiceRepository;
import com.kawin.supply_chain_system.repository.SalesOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Transactional
    public Invoice createInvoice(Long salesOrderId, int dueInDays) {
        SalesOrder order = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new RuntimeException("Sales order not found with id: " + salesOrderId));

        if (order.getStatus() != SalesOrderStatus.SHIPPED) {
            throw new RuntimeException("Only SHIPPED sales orders can be invoiced. Current status: " + order.getStatus());
        }

        Invoice invoice = new Invoice();
        invoice.setSalesOrder(order);
        invoice.setAmountDue(order.getTotalAmount());
        invoice.setAmountPaid(0.0);
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setDueDate(LocalDateTime.now().plusDays(dueInDays));
        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }
}