package com.kawin.supply_chain_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "paid_at")
    private LocalDateTime paidAt = LocalDateTime.now();
}