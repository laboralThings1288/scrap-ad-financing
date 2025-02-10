package com.scrapad.service;

import com.scrapad.dto.*;
import java.util.List;

public interface OfferService {
    String createOffer(CreateOfferRequest request);
    List<OfferResponse> getPendingOffersByOrg(String orgId);
    void requestFinancing(String offerId, FinancingRequest request);
    void acceptOffer(String offerId, AcceptOfferRequest request);
} 