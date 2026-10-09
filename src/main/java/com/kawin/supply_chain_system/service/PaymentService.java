package com.kawin.supply_chain_system.service;

import com.kawin.supply_chain_system.entity.Invoice;
import com.kawin.supply_chain_system.entity.InvoiceStatus;
import com.kawin.supply_chain_system.entity.Payment;
import com.kawin.supply_chain_system.repository.InvoiceRepository;
import com.kawin.supply_chain_system.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Transactional
    public Payment createPayment(Long invoiceId, Double amount, String paymentMethod) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Payment amount must be greater than 0");
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new RuntimeException("Payment method is required");
        }

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Invoice is already fully paid");
        }

        double remaining = round(invoice.getAmountDue() - invoice.getAmountPaid());
        double payment = round(amount);

        if (payment > remaining) {
            throw new RuntimeException("Payment exceeds amount owed. Remaining: " + remaining + ", payment: " + payment);
        }

        double newAmountPaid = round(invoice.getAmountPaid() + payment);
        invoice.setAmountPaid(newAmountPaid);
        if (round(invoice.getAmountDue() - newAmountPaid) <= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }
        invoiceRepository.save(invoice);

        Payment record = new Payment();
        record.setInvoice(invoice);
        record.setAmount(payment);
        record.setPaymentMethod(paymentMethod);
        record.setPaidAt(LocalDateTime.now());
        return paymentRepository.save(record);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    public List<Payment> getPaymentsByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}