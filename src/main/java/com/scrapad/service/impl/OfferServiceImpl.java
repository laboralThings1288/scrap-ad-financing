package com.scrapad.service.impl;

import com.scrapad.dto.*;
import com.scrapad.model.*;
import com.scrapad.repository.*;
import com.scrapad.service.OfferService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {
    
    private final AdRepository adRepository;
    private final OfferRepository offerRepository;
    private final OrganizationRepository organizationRepository;
    private final FinancingProviderRepository financingProviderRepository;

    @Override
    @Transactional
    public String createOffer(CreateOfferRequest request) {
        Ad ad = adRepository.findById(request.getAd())
            .orElseThrow(() -> new EntityNotFoundException("Ad not found"));
            
        Offer offer = new Offer();
        offer.setId(UUID.randomUUID().toString());
        offer.setAmount(request.getAmount());
        offer.setPrice(request.getPrice());
        offer.setPaymentMethod(request.getPaymentMethod());
        offer.setAccepted(0);
        
        return offerRepository.save(offer).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferResponse> getPendingOffersByOrg(String orgId) {
        return offerRepository.findByAccepted(0).stream()
            .map(this::mapToOfferResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void requestFinancing(String offerId, FinancingRequest request) {
        Offer offer = offerRepository.findById(offerId)
            .orElseThrow(() -> new EntityNotFoundException("Offer not found"));
            
        FinancingProvider provider = financingProviderRepository.findBySlug(request.getFinancingPartner())
            .orElseThrow(() -> new EntityNotFoundException("Financing provider not found"));
            
        offer.setFinancingProvider(provider);
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void acceptOffer(String offerId, AcceptOfferRequest request) {
        Offer offer = offerRepository.findById(offerId)
            .orElseThrow(() -> new EntityNotFoundException("Offer not found"));
            
        if (request.getFinancingPartner() != null) {
            FinancingProvider provider = financingProviderRepository.findBySlug(request.getFinancingPartner())
                .orElseThrow(() -> new EntityNotFoundException("Financing provider not found"));
            offer.setFinancingProvider(provider);
        }
        
        offer.setAccepted(1);
        offerRepository.save(offer);
    }

    private OfferResponse mapToOfferResponse(Offer offer) {
        OfferResponse response = new OfferResponse();
        response.setOfferId(offer.getId());
        response.setAmount(offer.getAmount());
        response.setPrice(offer.getPrice());
        response.setTotalPrice(offer.getTotalPrice());
        response.setPaymentMethod(offer.getPaymentMethod());
        return response;
    }
} 