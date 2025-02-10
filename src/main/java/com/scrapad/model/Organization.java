package com.scrapad.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "organizations")
public class Organization {
    @Id
    private String id;
    private String country;
    private LocalDateTime createdDate;
} 