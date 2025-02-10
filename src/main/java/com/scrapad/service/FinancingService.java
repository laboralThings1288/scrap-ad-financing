package com.scrapad.service;

import com.scrapad.model.Organization;
import com.scrapad.model.FinancingProvider;
import java.util.Optional;

public interface FinancingService {
    Optional<FinancingProvider> determineFinancingProvider(Organization organization);
    Integer calculateFinancingAmount(Integer totalAmount, FinancingProvider provider);
} 