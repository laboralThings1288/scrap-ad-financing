package com.scrapad.dto;

import lombok.Data;

@Data
public class OfferResponse {
    private String offerId;
    private String ad;
    private Integer amount;
    private Integer price;
    private Integer totalPrice;
    private String paymentMethod;
} 