package com.scrapad.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FinancingRequest {
    @NotNull
    private String financingPartner;
    
    @NotNull
    private Integer totalToPerceive;
} 