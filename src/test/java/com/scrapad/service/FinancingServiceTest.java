package com.scrapad.service;

import com.scrapad.model.Organization;
import com.scrapad.model.FinancingProvider;
import com.scrapad.repository.FinancingProviderRepository;
import com.scrapad.repository.OrganizationRepository;
import com.scrapad.service.impl.FinancingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FinancingServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private FinancingProviderRepository financingProviderRepository;

    @InjectMocks
    private FinancingServiceImpl financingService;

    private Organization organization;
    private FinancingProvider bankProvider;
    private FinancingProvider fintechProvider;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setId("org1");
        organization.setCreatedDate(LocalDateTime.now().minusYears(2));

        bankProvider = new FinancingProvider();
        bankProvider.setId(1L);
        bankProvider.setSlug("financing_bank");
        bankProvider.setFinancingPercentage(5);

        fintechProvider = new FinancingProvider();
        fintechProvider.setId(2L);
        fintechProvider.setSlug("financing_fintech");
        fintechProvider.setFinancingPercentage(7);
    }

    @Test
    void shouldReturnBankProviderForSpanishOrganization() {
        // Given
        organization.setCountry("SPAIN");
        when(organizationRepository.countPublishedAds(any())).thenReturn(15000L);
        when(financingProviderRepository.findBySlug("financing_bank"))
            .thenReturn(Optional.of(bankProvider));

        // When
        Optional<FinancingProvider> result = financingService.determineFinancingProvider(organization);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getSlug()).isEqualTo("financing_bank");
    }

    @Test
    void shouldReturnFintechProviderForNonEuropeanOrganization() {
        // Given
        organization.setCountry("USA");
        when(organizationRepository.countPublishedAds(any())).thenReturn(15000L);
        when(financingProviderRepository.findBySlug("financing_fintech"))
            .thenReturn(Optional.of(fintechProvider));

        // When
        Optional<FinancingProvider> result = financingService.determineFinancingProvider(organization);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getSlug()).isEqualTo("financing_fintech");
    }

    @Test
    void shouldReturnEmptyWhenNotEnoughAds() {
        // Given
        organization.setCountry("SPAIN");
        when(organizationRepository.countPublishedAds(any())).thenReturn(5000L);

        // When
        Optional<FinancingProvider> result = financingService.determineFinancingProvider(organization);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldCalculateFinancingAmountCorrectly() {
        // Given
        Integer totalAmount = 10000;
        bankProvider.setFinancingPercentage(5);

        // When
        Integer result = financingService.calculateFinancingAmount(totalAmount, bankProvider);

        // Then
        assertThat(result).isEqualTo(9500); // 10000 - 5% = 9500
    }

    @Test
    void shouldReturnOriginalAmountWhenNoProvider() {
        // Given
        Integer totalAmount = 10000;

        // When
        Integer result = financingService.calculateFinancingAmount(totalAmount, null);

        // Then
        assertThat(result).isEqualTo(totalAmount);
    }
} 