package com.guce.pcs.api;

import com.guce.pcs.api.dto.ContainerMovementRequest;
import com.guce.pcs.api.dto.ContainerMovementResponse;
import com.guce.pcs.api.dto.MainleveeNotificationRequest;
import com.guce.pcs.domain.ContainerMovement;
import com.guce.pcs.domain.ContainerMovementRepository;
import com.guce.pcs.domain.MovementType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pcs")
@Tag(name = "PCS")
public class PcsController {

    private final ContainerMovementRepository repository;

    public PcsController(ContainerMovementRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/container-movements")
    @Operation(summary = "Enregistrer un mouvement de conteneur")
    public ResponseEntity<ContainerMovementResponse> recordMovement(@Valid @RequestBody ContainerMovementRequest request) {
        ContainerMovement m = new ContainerMovement();
        m.setId(UUID.randomUUID());
        m.setContainerNumber(request.containerNumber());
        m.setDeclarationId(request.declarationId());
        m.setMovementType(request.movementType());
        m.setOccurredAt(parse(request.occurredAt()));
        m.setCreatedAt(Instant.now());
        ContainerMovement saved = repository.save(m);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ContainerMovementResponse(saved.getId(), saved.getContainerNumber(), saved.getDeclarationId(), saved.getMovementType().name(), saved.getOccurredAt()));
    }

    @PostMapping("/notifications/mainlevee")
    @Operation(summary = "Accusé de réception mainlevée vers le port (simulation)")
    public ResponseEntity<Void> acknowledgeMainlevee(@Valid @RequestBody MainleveeNotificationRequest request) {
        return ResponseEntity.accepted().build();
    }

    private static Instant parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return Instant.now();
        }
        try {
            return OffsetDateTime.parse(raw).toInstant();
        } catch (Exception e) {
            return Instant.parse(raw);
        }
    }
}
