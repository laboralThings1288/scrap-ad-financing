package com.scrapad.service.impl;

import com.scrapad.model.Organization;
import com.scrapad.model.FinancingProvider;
import com.scrapad.repository.FinancingProviderRepository;
import com.scrapad.repository.OrganizationRepository;
import com.scrapad.service.FinancingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FinancingServiceImpl implements FinancingService {

    private final OrganizationRepository organizationRepository;
    private final FinancingProviderRepository financingProviderRepository;
    
    private static final String FINANCING_BANK = "financing_bank";
    private static final String FINANCING_FINTECH = "financing_fintech";
    private static final long MIN_ADS_PUBLISHED = 10000;

    @Override
    public Optional<FinancingProvider> determineFinancingProvider(Organization organization) {
        if (organization == null) {
            return Optional.empty();
        }

        Long publishedAds = organizationRepository.countPublishedAds(organization);
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        if (publishedAds > MIN_ADS_PUBLISHED && organization.getCreatedDate().isBefore(oneYearAgo)) {
            String providerSlug;
            
            if (Arrays.asList("SPAIN", "FRANCE").contains(organization.getCountry())) {
                providerSlug = FINANCING_BANK;
            } else {
                providerSlug = FINANCING_FINTECH;
            }
            
            return financingProviderRepository.findBySlug(providerSlug);
        }

        return Optional.empty();
    }

    @Override
    public Integer calculateFinancingAmount(Integer totalAmount, FinancingProvider provider) {
        if (totalAmount == null || provider == null) {
            return totalAmount;
        }
        
        // Apply financing percentage (fee)
        return totalAmount - (totalAmount * provider.getFinancingPercentage() / 100);
    }
} 