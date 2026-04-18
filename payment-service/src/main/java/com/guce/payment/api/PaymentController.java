package com.guce.payment.api;

import com.guce.payment.api.dto.ConfirmPaymentRequest;
import com.guce.payment.api.dto.PaymentResponse;
import com.guce.payment.api.dto.TaxCalculationRequest;
import com.guce.payment.api.dto.TaxCalculationResponse;
import com.guce.payment.config.GuceProperties;
import com.guce.payment.domain.Payment;
import com.guce.payment.domain.PaymentChannel;
import com.guce.payment.domain.PaymentRepository;
import com.guce.payment.domain.PaymentStatus;
import com.guce.payment.domain.TaxAssessment;
import com.guce.payment.domain.TaxAssessmentRepository;
import com.guce.payment.messaging.TopicPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Paiements et taxes")
public class PaymentController {

    private final TaxAssessmentRepository taxRepository;
    private final PaymentRepository paymentRepository;
    private final TopicPublisher topicPublisher;
    private final GuceProperties guceProperties;

    public PaymentController(
            TaxAssessmentRepository taxRepository,
            PaymentRepository paymentRepository,
            TopicPublisher topicPublisher,
            GuceProperties guceProperties) {
        this.taxRepository = taxRepository;
        this.paymentRepository = paymentRepository;
        this.topicPublisher = topicPublisher;
        this.guceProperties = guceProperties;
    }

    @PostMapping("/taxes/calculate")
    @Operation(summary = "Calculer les droits et taxes", description = "Publie l'événement dau.taxee")
    public ResponseEntity<TaxCalculationResponse> calculateTaxes(@Valid @RequestBody TaxCalculationRequest request) {
        BigDecimal amount = request.amountXof() != null
                ? request.amountXof()
                : BigDecimal.valueOf(125_000);

        TaxAssessment t = new TaxAssessment();
        t.setId(UUID.randomUUID());
        t.setDeclarationId(request.declarationId());
        t.setAmountXof(amount);
        t.setCurrency("XOF");
        t.setComputedAt(Instant.now());
        TaxAssessment saved = taxRepository.save(t);

        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventType", "DAU_TAXEE");
        event.put("declarationId", saved.getDeclarationId().toString());
        event.put("taxAssessmentId", saved.getId().toString());
        event.put("amountXof", saved.getAmountXof().toPlainString());
        event.put("currency", saved.getCurrency());
        event.put("occurredAt", saved.getComputedAt().toString());
        topicPublisher.publish(
                guceProperties.getKafka().getTopicDauTaxee(),
                saved.getDeclarationId().toString(),
                event);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TaxCalculationResponse(saved.getId(), saved.getDeclarationId(), saved.getAmountXof(), saved.getCurrency()));
    }

    @PostMapping("/{declarationId}/confirm")
    @Operation(summary = "Confirmer un paiement (mobile / Trésor)", description = "Publie l'événement paiement.confirme")
    public ResponseEntity<PaymentResponse> confirm(
            @PathVariable UUID declarationId,
            @Valid @RequestBody ConfirmPaymentRequest request) {
        Payment p = new Payment();
        p.setId(UUID.randomUUID());
        p.setDeclarationId(declarationId);
        p.setAmount(request.amount());
        p.setChannel(request.channel());
        p.setStatus(PaymentStatus.CONFIRME);
        p.setCreatedAt(Instant.now());
        Payment saved = paymentRepository.save(p);

        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventType", "PAIEMENT_CONFIRME");
        event.put("declarationId", saved.getDeclarationId().toString());
        event.put("paymentId", saved.getId().toString());
        event.put("channel", saved.getChannel().name());
        event.put("amount", saved.getAmount().toPlainString());
        event.put("occurredAt", saved.getCreatedAt().toString());
        topicPublisher.publish(
                guceProperties.getKafka().getTopicPaiementConfirme(),
                saved.getDeclarationId().toString(),
                event);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PaymentResponse(saved.getId(), saved.getDeclarationId(), saved.getAmount(), saved.getChannel().name(), saved.getStatus().name()));
    }
}
