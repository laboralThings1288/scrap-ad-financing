package com.scrapad.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ads")
public class Ad {
    @Id
    private String id;
    private Integer amount;
    private Integer price;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Organization organization;
} 