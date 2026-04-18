package com.guce.operator.api;

import com.guce.operator.api.dto.OperatorRequest;
import com.guce.operator.api.dto.OperatorResponse;
import com.guce.operator.domain.OperatorAccount;
import com.guce.operator.domain.OperatorRepository;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/operators")
@Tag(name = "Opérateurs")
public class OperatorController {

    private final OperatorRepository repository;

    public OperatorController(OperatorRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @Operation(summary = "Enregistrer un opérateur / transitaire")
    public ResponseEntity<OperatorResponse> register(@Valid @RequestBody OperatorRequest request) {
        OperatorAccount o = new OperatorAccount();
        o.setId(UUID.randomUUID());
        o.setLegalName(request.legalName());
        o.setEmail(request.email());
        o.setRole(request.role());
        o.setCreatedAt(Instant.now());
        OperatorAccount saved = repository.save(o);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new OperatorResponse(saved.getId(), saved.getLegalName(), saved.getEmail(), saved.getRole(), saved.getCreatedAt()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un opérateur")
    public ResponseEntity<OperatorResponse> get(@PathVariable UUID id) {
        return repository.findById(id)
                .map(o -> ResponseEntity.ok(new OperatorResponse(o.getId(), o.getLegalName(), o.getEmail(), o.getRole(), o.getCreatedAt())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
