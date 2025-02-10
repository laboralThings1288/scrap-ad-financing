package com.scrapad.controller;

import com.scrapad.dto.*;
import com.scrapad.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
@Tag(name = "Offers", description = "Endpoints for managing offers and financing")
public class OfferController {

    private final OfferService offerService;

    @PostMapping
    @Operation(
        summary = "Create a new offer",
        description = "Creates a new offer for an existing ad. The amount and price are multiplied by 100 to avoid decimals. " +
            "For example, an amount of 150 represents 1.5 units."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Offer created successfully"),
        @ApiResponse(responseCode = "404", description = "Ad not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<String> createOffer(
            @Valid @RequestBody 
            @Parameter(description = "Offer details", required = true) 
            CreateOfferRequest request) {
        String offerId = offerService.createOffer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(offerId);
    }

    @GetMapping("/organization/{orgId}")
    @Operation(
        summary = "Get pending offers",
        description = "Retrieves all pending (not accepted) offers for a specific organization. " +
            "The response includes calculated total prices and financing details if available."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of pending offers",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OfferResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    public ResponseEntity<List<OfferResponse>> getPendingOffers(
            @Parameter(description = "Organization ID", example = "org123", required = true)
            @PathVariable String orgId) {
        List<OfferResponse> offers = offerService.getPendingOffersByOrg(orgId);
        return ResponseEntity.ok(offers);
    }

    @PostMapping("/{offerId}/financing")
    @Operation(
        summary = "Request financing",
        description = "Request financing for a specific offer. The financing partner is determined based on organization rules. " +
            "The total amount to perceive will be the original amount minus the financing fee."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Financing requested successfully"),
        @ApiResponse(responseCode = "404", description = "Offer not found"),
        @ApiResponse(responseCode = "400", description = "Invalid financing request or organization not eligible")
    })
    public ResponseEntity<Void> requestFinancing(
            @Parameter(description = "Offer ID", example = "offer123", required = true)
            @PathVariable String offerId,
            @Valid @RequestBody 
            @Parameter(description = "Financing details", required = true)
            FinancingRequest request) {
        offerService.requestFinancing(offerId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{offerId}/accept")
    @Operation(
        summary = "Accept offer",
        description = "Accept an offer with optional financing. If financing was previously requested or is included " +
            "in this request, it will be applied to the offer. Otherwise, the offer will be accepted without financing."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Offer accepted successfully"),
        @ApiResponse(responseCode = "404", description = "Offer not found"),
        @ApiResponse(responseCode = "400", description = "Invalid acceptance request")
    })
    public ResponseEntity<Void> acceptOffer(
            @Parameter(description = "Offer ID", example = "offer123", required = true)
            @PathVariable String offerId,
            @RequestBody(required = false)
            @Parameter(description = "Acceptance details with optional financing")
            AcceptOfferRequest request) {
        offerService.acceptOffer(offerId, request != null ? request : new AcceptOfferRequest());
        return ResponseEntity.ok().build();
    }
} 