package com.scrapad.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOfferRequest {
    @NotNull
    private String ad;
    
    @NotNull
    private Integer amount;
    
    @NotNull
    private Integer price;
    
    @NotNull
    private String paymentMethod;
} 