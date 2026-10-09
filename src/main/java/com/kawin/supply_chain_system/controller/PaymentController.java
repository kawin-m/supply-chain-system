package com.kawin.supply_chain_system.controller;

import com.kawin.supply_chain_system.entity.Payment;
import com.kawin.supply_chain_system.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public Payment createPayment(@RequestBody Map<String, Object> request) {
        Long invoiceId = Long.valueOf(request.get("invoiceId").toString());
        Double amount = Double.valueOf(request.get("amount").toString());
        String paymentMethod = (String) request.get("paymentMethod");
        return paymentService.createPayment(invoiceId, amount, paymentMethod);
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping("/invoice/{invoiceId}")
    public List<Payment> getPaymentsByInvoice(@PathVariable Long invoiceId) {
        return paymentService.getPaymentsByInvoice(invoiceId);
    }
}