package com.scrapad.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scrapad.dto.*;
import com.scrapad.model.*;
import com.scrapad.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OfferFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private FinancingProviderRepository financingProviderRepository;

    @Autowired
    private OfferRepository offerRepository;

    private Organization organization;
    private Ad ad;
    private FinancingProvider bankProvider;

    @BeforeEach
    void setUp() {
        // Create test organization
        organization = new Organization();
        organization.setId("test-org");
        organization.setCountry("SPAIN");
        organization.setCreatedDate(LocalDateTime.now().minusYears(2));
        organizationRepository.save(organization);

        // Create test ad
        ad = new Ad();
        ad.setId("test-ad");
        ad.setAmount(100);
        ad.setPrice(1000);
        ad.setOrganization(organization);
        adRepository.save(ad);

        // Create financing provider
        bankProvider = new FinancingProvider();
        bankProvider.setSlug("financing_bank");
        bankProvider.setPaymentMethod("100_in_unload");
        bankProvider.setFinancingPercentage(5);
        financingProviderRepository.save(bankProvider);
    }

    @Test
    void shouldCompleteFullOfferFlow() throws Exception {
        // 1. Create offer
        CreateOfferRequest createRequest = new CreateOfferRequest();
        createRequest.setAd(ad.getId());
        createRequest.setAmount(100);
        createRequest.setPrice(1000);
        createRequest.setPaymentMethod("100_in_unload");

        String response = mockMvc.perform(post("/api/v1/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String offerId = response.replace("\"", "");

        // 2. Verify offer appears in pending offers
        mockMvc.perform(get("/api/v1/offers/organization/" + organization.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].offerId").value(offerId))
                .andExpect(jsonPath("$[0].totalPrice").value(100000));

        // 3. Request financing
        FinancingRequest financingRequest = new FinancingRequest();
        financingRequest.setFinancingPartner("financing_bank");
        financingRequest.setTotalToPerceive(95000); // 100000 - 5%

        mockMvc.perform(post("/api/v1/offers/" + offerId + "/financing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(financingRequest)))
                .andExpect(status().isOk());

        // 4. Accept offer with financing
        AcceptOfferRequest acceptRequest = new AcceptOfferRequest();
        acceptRequest.setFinancingPartner("financing_bank");

        mockMvc.perform(post("/api/v1/offers/" + offerId + "/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(acceptRequest)))
                .andExpect(status().isOk());

        // 5. Verify final state
        Offer finalOffer = offerRepository.findById(offerId).orElseThrow();
        assertThat(finalOffer.getAccepted()).isEqualTo(1);
        assertThat(finalOffer.getFinancingProvider()).isNotNull();
        assertThat(finalOffer.getFinancingProvider().getSlug()).isEqualTo("financing_bank");
    }

    @Test
    void shouldHandleOfferWithoutFinancing() throws Exception {
        // 1. Create offer
        CreateOfferRequest createRequest = new CreateOfferRequest();
        createRequest.setAd(ad.getId());
        createRequest.setAmount(50);
        createRequest.setPrice(500);
        createRequest.setPaymentMethod("payment_in_shipping");

        String response = mockMvc.perform(post("/api/v1/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String offerId = response.replace("\"", "");

        // 2. Accept offer without financing
        mockMvc.perform(post("/api/v1/offers/" + offerId + "/accept")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 3. Verify final state
        Offer finalOffer = offerRepository.findById(offerId).orElseThrow();
        assertThat(finalOffer.getAccepted()).isEqualTo(1);
        assertThat(finalOffer.getFinancingProvider()).isNull();
    }
} 