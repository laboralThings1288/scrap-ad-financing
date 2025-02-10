package com.scrapad.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "fnancing_providers")
public class FinancingProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String slug;
    private String paymentMethod;
    private Integer financingPercentage;
} 