package com.guce.declaration.infrastructure.web;

import com.guce.declaration.application.port.in.SubmitDeclarationCommand;
import com.guce.declaration.application.port.in.SubmitDeclarationUseCase;
import com.guce.declaration.application.service.GetDeclarationService;
import com.guce.declaration.domain.Declaration;
import com.guce.declaration.infrastructure.web.dto.DeclarationResponse;
import com.guce.declaration.infrastructure.web.dto.SubmitDeclarationRequest;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/declarations")
@Tag(name = "Déclarations", description = "Soumission et consultation des DAU")
public class DeclarationRestController {

    private final SubmitDeclarationUseCase submitDeclarationUseCase;
    private final GetDeclarationService getDeclarationService;

    public DeclarationRestController(
            SubmitDeclarationUseCase submitDeclarationUseCase,
            GetDeclarationService getDeclarationService) {
        this.submitDeclarationUseCase = submitDeclarationUseCase;
        this.getDeclarationService = getDeclarationService;
    }

    @PostMapping
    @Operation(summary = "Soumettre une DAU", description = "Persiste la déclaration et publie l'événement dau.soumise (Kafka ou simulation)")
    public ResponseEntity<DeclarationResponse> submit(@Valid @RequestBody SubmitDeclarationRequest request) {
        SubmitDeclarationCommand command = new SubmitDeclarationCommand(
                request.correlationId(),
                request.declarantId(),
                request.customsOfficeCode(),
                request.referenceNumber(),
                request.payloadJson());
        Declaration saved = submitDeclarationUseCase.submit(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une DAU par identifiant")
    public ResponseEntity<DeclarationResponse> getById(@PathVariable UUID id) {
        return getDeclarationService.getById(id)
                .map(d -> ResponseEntity.ok(toResponse(d)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private static DeclarationResponse toResponse(Declaration d) {
        return new DeclarationResponse(
                d.getId(),
                d.getCorrelationId(),
                d.getDeclarantId(),
                d.getCustomsOfficeCode(),
                d.getReferenceNumber(),
                d.getStatus().name(),
                d.getCreatedAt());
    }
}
