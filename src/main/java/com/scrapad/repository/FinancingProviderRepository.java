package com.scrapad.repository;

import com.scrapad.model.FinancingProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FinancingProviderRepository extends JpaRepository<FinancingProvider, Long> {
    Optional<FinancingProvider> findBySlug(String slug);
} 