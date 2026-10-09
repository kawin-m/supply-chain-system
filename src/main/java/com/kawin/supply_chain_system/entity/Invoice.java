package com.kawin.supply_chain_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "sales_order_id", nullable = false, unique = true)
    private SalesOrder salesOrder;

    @Column(name = "amount_due", nullable = false)
    private Double amountDue;

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.UNPAID;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}