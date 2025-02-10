package com.scrapad.repository;

import com.scrapad.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {
    @Query("SELECT COUNT(a) FROM Ad a WHERE a.organization = ?1")
    Long countPublishedAds(Organization organization);
} 