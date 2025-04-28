package com.dms.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerId;

    @Column
    private String driverId;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String status; // e.g., "Unassigned", "Assigned", "In Progress", "Completed"
} 