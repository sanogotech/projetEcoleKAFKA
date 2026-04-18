package com.guce.manifest.api;

import com.guce.manifest.api.dto.ManifestRequest;
import com.guce.manifest.api.dto.ManifestResponse;
import com.guce.manifest.config.GuceProperties;
import com.guce.manifest.domain.ManifestEntity;
import com.guce.manifest.domain.ManifestRepository;
import com.guce.manifest.messaging.TopicPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manifests")
@Tag(name = "Manifestes")
public class ManifestController {

    private final ManifestRepository repository;
    private final TopicPublisher topicPublisher;
    private final GuceProperties guceProperties;

    public ManifestController(
            ManifestRepository repository,
            TopicPublisher topicPublisher,
            GuceProperties guceProperties) {
        this.repository = repository;
        this.topicPublisher = topicPublisher;
        this.guceProperties = guceProperties;
    }

    @PostMapping
    @Operation(summary = "Enregistrer un e-manifeste", description = "Publie l'événement manifeste.recu")
    public ResponseEntity<ManifestResponse> create(@Valid @RequestBody ManifestRequest request) {
        ManifestEntity e = new ManifestEntity();
        e.setId(UUID.randomUUID());
        e.setManifestNumber(request.manifestNumber());
        e.setVesselName(request.vesselName());
        e.setArrivalDate(parseArrival(request.arrivalDate()));
        e.setLinesJson(request.linesJson());
        e.setCreatedAt(Instant.now());
        ManifestEntity saved = repository.save(e);

        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventType", "MANIFESTE_RECU");
        event.put("manifestId", saved.getId().toString());
        event.put("manifestNumber", saved.getManifestNumber());
        event.put("occurredAt", saved.getCreatedAt().toString());
        topicPublisher.publish(
                guceProperties.getKafka().getTopicManifesteRecu(),
                saved.getId().toString(),
                event);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un manifeste")
    public ResponseEntity<ManifestResponse> get(@PathVariable UUID id) {
        return repository.findById(id).map(m -> ResponseEntity.ok(toResponse(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private static ManifestResponse toResponse(ManifestEntity e) {
        return new ManifestResponse(e.getId(), e.getManifestNumber(), e.getVesselName(), e.getArrivalDate(), e.getCreatedAt());
    }

    private static Instant parseArrival(String raw) {
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
