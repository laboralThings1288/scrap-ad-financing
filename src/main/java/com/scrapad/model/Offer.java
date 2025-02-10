package com.scrapad.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "offers")
public class Offer {
    @Id
    private String id;
    private String paymentMethod;
    
    @ManyToOne
    @JoinColumn(name = "financing_privder")
    private FinancingProvider financingProvider;
    
    private Integer amount;
    private Integer accepted;
    private Integer price;
    
    @Transient
    public Integer getTotalPrice() {
        return amount * price;
    }
} 