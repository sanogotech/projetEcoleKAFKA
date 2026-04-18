package com.guce.inspection.api;

import com.guce.inspection.api.dto.ChannelOrientationResponse;
import com.guce.inspection.api.dto.InspectionRequest;
import com.guce.inspection.api.dto.InspectionResponse;
import com.guce.inspection.api.dto.OrientRequest;
import com.guce.inspection.domain.InspectionCase;
import com.guce.inspection.domain.InspectionCaseRepository;
import com.guce.inspection.domain.InspectionChannel;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inspections")
@Tag(name = "Inspection")
public class InspectionController {

    private final InspectionCaseRepository repository;

    public InspectionController(InspectionCaseRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/orient")
    @Operation(summary = "Orientation canal (vert / orange / rouge)", description = "Règle simplifiée : hash du déclarationId")
    public ChannelOrientationResponse orient(@Valid @RequestBody OrientRequest request) {
        InspectionChannel channel = suggestChannel(request.declarationId());
        return new ChannelOrientationResponse(request.declarationId(), channel.name(), "Score démo basé sur l'identifiant");
    }

    @PostMapping
    @Operation(summary = "Planifier une inspection")
    public ResponseEntity<InspectionResponse> schedule(@Valid @RequestBody InspectionRequest request) {
        InspectionCase c = new InspectionCase();
        c.setId(UUID.randomUUID());
        c.setDeclarationId(request.declarationId());
        c.setChannel(request.channel());
        c.setNotes(request.notes());
        c.setScheduledAt(parse(request.scheduledAt()));
        c.setCreatedAt(Instant.now());
        InspectionCase saved = repository.save(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un dossier d'inspection")
    public ResponseEntity<InspectionResponse> get(@PathVariable UUID id) {
        return repository.findById(id).map(i -> ResponseEntity.ok(toResponse(i)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private static InspectionResponse toResponse(InspectionCase c) {
        return new InspectionResponse(c.getId(), c.getDeclarationId(), c.getChannel().name(), c.getNotes(), c.getScheduledAt(), c.getCreatedAt());
    }

    private static InspectionChannel suggestChannel(UUID declarationId) {
        int h = Math.abs(declarationId.toString().hashCode() % 3);
        return switch (h) {
            case 0 -> InspectionChannel.VERT;
            case 1 -> InspectionChannel.ORANGE;
            default -> InspectionChannel.ROUGE;
        };
    }

    private static Instant parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(raw).toInstant();
        } catch (Exception e) {
            return Instant.parse(raw);
        }
    }
}
