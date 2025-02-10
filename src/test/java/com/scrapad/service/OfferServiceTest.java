package com.scrapad.service;

import com.scrapad.dto.*;
import com.scrapad.model.*;
import com.scrapad.repository.*;
import com.scrapad.service.impl.OfferServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private FinancingProviderRepository financingProviderRepository;

    @InjectMocks
    private OfferServiceImpl offerService;

    private Ad ad;
    private Offer offer;
    private FinancingProvider provider;
    private CreateOfferRequest createRequest;

    @BeforeEach
    void setUp() {
        ad = new Ad();
        ad.setId("ad1");
        ad.setAmount(100);
        ad.setPrice(1000);

        offer = new Offer();
        offer.setId("offer1");
        offer.setAmount(100);
        offer.setPrice(1000);
        offer.setPaymentMethod("100_in_unload");
        offer.setAccepted(0);

        provider = new FinancingProvider();
        provider.setId(1L);
        provider.setSlug("financing_bank");
        provider.setFinancingPercentage(5);

        createRequest = new CreateOfferRequest();
        createRequest.setAd("ad1");
        createRequest.setAmount(100);
        createRequest.setPrice(1000);
        createRequest.setPaymentMethod("100_in_unload");
    }

    @Test
    void shouldCreateOffer() {
        // Given
        when(adRepository.findById(any())).thenReturn(Optional.of(ad));
        when(offerRepository.save(any())).thenReturn(offer);

        // When
        String offerId = offerService.createOffer(createRequest);

        // Then
        assertThat(offerId).isNotNull();
        verify(offerRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAdNotFound() {
        // Given
        when(adRepository.findById(any())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> {
            offerService.createOffer(createRequest);
        });
    }

    @Test
    void shouldGetPendingOffers() {
        // Given
        when(offerRepository.findByAccepted(0))
            .thenReturn(Arrays.asList(offer));

        // When
        List<OfferResponse> responses = offerService.getPendingOffersByOrg("org1");

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getOfferId()).isEqualTo(offer.getId());
        assertThat(responses.get(0).getTotalPrice()).isEqualTo(offer.getTotalPrice());
    }

    @Test
    void shouldRequestFinancing() {
        // Given
        FinancingRequest request = new FinancingRequest();
        request.setFinancingPartner("financing_bank");
        request.setTotalToPerceive(100000);

        when(offerRepository.findById(any())).thenReturn(Optional.of(offer));
        when(financingProviderRepository.findBySlug(any())).thenReturn(Optional.of(provider));
        when(offerRepository.save(any())).thenReturn(offer);

        // When
        offerService.requestFinancing("offer1", request);

        // Then
        verify(offerRepository).save(any());
        assertThat(offer.getFinancingProvider()).isEqualTo(provider);
    }

    @Test
    void shouldAcceptOffer() {
        // Given
        AcceptOfferRequest request = new AcceptOfferRequest();
        request.setFinancingPartner("financing_bank");

        when(offerRepository.findById(any())).thenReturn(Optional.of(offer));
        when(financingProviderRepository.findBySlug(any())).thenReturn(Optional.of(provider));
        when(offerRepository.save(any())).thenReturn(offer);

        // When
        offerService.acceptOffer("offer1", request);

        // Then
        verify(offerRepository).save(any());
        assertThat(offer.getAccepted()).isEqualTo(1);
        assertThat(offer.getFinancingProvider()).isEqualTo(provider);
    }
} 