package com.guce.audit.api;

import com.guce.audit.api.dto.AuditRecordRequest;
import com.guce.audit.api.dto.AuditRecordResponse;
import com.guce.audit.domain.AuditRecord;
import com.guce.audit.domain.AuditRecordRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
@Tag(name = "Audit")
public class AuditController {

    private final AuditRecordRepository repository;

    public AuditController(AuditRecordRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/records")
    @Operation(summary = "Enregistrer un événement d'audit")
    public ResponseEntity<AuditRecordResponse> ingest(@Valid @RequestBody AuditRecordRequest request) {
        AuditRecord r = new AuditRecord();
        r.setId(UUID.randomUUID());
        r.setDeclarationId(request.declarationId());
        r.setEventType(request.eventType());
        r.setActor(request.actor());
        r.setPayloadJson(request.payloadJson());
        r.setOccurredAt(parse(request.occurredAt()));
        AuditRecord saved = repository.save(r);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/records")
    @Operation(summary = "Historique par déclaration")
    public List<AuditRecordResponse> search(@RequestParam UUID declarationId) {
        return repository.findByDeclarationIdOrderByOccurredAtDesc(declarationId).stream()
                .map(AuditController::toResponse)
                .toList();
    }

    private static AuditRecordResponse toResponse(AuditRecord r) {
        return new AuditRecordResponse(r.getId(), r.getDeclarationId(), r.getEventType(), r.getActor(), r.getPayloadJson(), r.getOccurredAt());
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
