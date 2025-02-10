package com.scrapad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scrapad.dto.*;
import com.scrapad.service.OfferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferController.class)
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OfferService offerService;

    @Test
    void shouldCreateOffer() throws Exception {
        // Given
        CreateOfferRequest request = new CreateOfferRequest();
        request.setAd("ad1");
        request.setAmount(100);
        request.setPrice(1000);
        request.setPaymentMethod("100_in_unload");

        when(offerService.createOffer(any())).thenReturn("offer1");

        // When/Then
        mockMvc.perform(post("/api/v1/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("offer1"));
    }

    @Test
    void shouldGetPendingOffers() throws Exception {
        // Given
        OfferResponse response = new OfferResponse();
        response.setOfferId("offer1");
        response.setAmount(100);
        response.setPrice(1000);
        response.setTotalPrice(100000);
        response.setPaymentMethod("100_in_unload");

        when(offerService.getPendingOffersByOrg("org1"))
            .thenReturn(Arrays.asList(response));

        // When/Then
        mockMvc.perform(get("/api/v1/offers/organization/org1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].offerId").value("offer1"))
                .andExpect(jsonPath("$[0].totalPrice").value(100000));
    }

    @Test
    void shouldRequestFinancing() throws Exception {
        // Given
        FinancingRequest request = new FinancingRequest();
        request.setFinancingPartner("financing_bank");
        request.setTotalToPerceive(100000);

        // When/Then
        mockMvc.perform(post("/api/v1/offers/offer1/financing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAcceptOffer() throws Exception {
        // Given
        AcceptOfferRequest request = new AcceptOfferRequest();
        request.setFinancingPartner("financing_bank");

        // When/Then
        mockMvc.perform(post("/api/v1/offers/offer1/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAcceptOfferWithoutFinancing() throws Exception {
        // When/Then
        mockMvc.perform(post("/api/v1/offers/offer1/accept")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
} 