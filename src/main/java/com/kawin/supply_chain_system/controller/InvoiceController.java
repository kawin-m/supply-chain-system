package com.kawin.supply_chain_system.controller;

import com.kawin.supply_chain_system.entity.Invoice;
import com.kawin.supply_chain_system.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public Invoice createInvoice(@RequestBody Map<String, Object> request) {
        Long salesOrderId = Long.valueOf(request.get("salesOrderId").toString());
        int dueInDays = request.get("dueInDays") != null
                ? Integer.parseInt(request.get("dueInDays").toString())
                : 30;
        return invoiceService.createInvoice(salesOrderId, dueInDays);
    }

    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public Invoice getInvoice(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }
}