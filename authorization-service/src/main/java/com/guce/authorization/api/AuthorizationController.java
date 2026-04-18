package com.guce.authorization.api;

import com.guce.authorization.api.dto.AuthorizationRequest;
import com.guce.authorization.api.dto.AuthorizationResponse;
import com.guce.authorization.api.dto.StatusUpdateRequest;
import com.guce.authorization.domain.ApiAuthorization;
import com.guce.authorization.domain.ApiAuthorizationRepository;
import com.guce.authorization.domain.AuthorizationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authorizations")
@Tag(name = "Autorisations préalables (API)")
public class AuthorizationController {

    private final ApiAuthorizationRepository repository;

    public AuthorizationController(ApiAuthorizationRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @Operation(summary = "Créer une demande d'autorisation préalable")
    public ResponseEntity<AuthorizationResponse> create(@Valid @RequestBody AuthorizationRequest request) {
        ApiAuthorization e = new ApiAuthorization();
        e.setId(UUID.randomUUID());
        e.setApplicantId(request.applicantId());
        e.setGoodsDescription(request.goodsDescription());
        e.setStatus(AuthorizationStatus.DEMANDEE);
        e.setExpiresAt(parseInstant(request.expiresAt()));
        e.setCreatedAt(Instant.now());
        ApiAuthorization saved = repository.save(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une demande")
    public ResponseEntity<AuthorizationResponse> get(@PathVariable UUID id) {
        return repository.findById(id).map(a -> ResponseEntity.ok(toResponse(a)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut (workflow multi-administrations)")
    public ResponseEntity<AuthorizationResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return repository.findById(id)
                .map(e -> {
                    e.setStatus(request.status());
                    return ResponseEntity.ok(toResponse(repository.save(e)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private static AuthorizationResponse toResponse(ApiAuthorization e) {
        return new AuthorizationResponse(
                e.getId(),
                e.getApplicantId(),
                e.getGoodsDescription(),
                e.getStatus().name(),
                e.getExpiresAt(),
                e.getCreatedAt());
    }

    private static Instant parseInstant(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(raw).toInstant();
        } catch (Exception ex) {
            return Instant.parse(raw);
        }
    }
}
