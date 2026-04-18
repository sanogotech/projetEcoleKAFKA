package com.guce.declaration.infrastructure.messaging;

import com.guce.declaration.application.port.out.DauSoumisePublisher;
import com.guce.declaration.config.GuceProperties;
import com.guce.declaration.domain.Declaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Mode dev : pas de broker Kafka — journalisation et buffer consultable (tests / démo).
 */
@Component
@ConditionalOnProperty(name = "guce.mode", havingValue = "dev", matchIfMissing = true)
public class SimulationDauSoumisePublisher implements DauSoumisePublisher {

    private static final Logger log = LoggerFactory.getLogger(SimulationDauSoumisePublisher.class);

    private final GuceProperties guceProperties;
    private final List<DauSoumiseEventPayload> lastEvents = new CopyOnWriteArrayList<>();

    public SimulationDauSoumisePublisher(GuceProperties guceProperties) {
        this.guceProperties = guceProperties;
    }

    @Override
    public void publish(Declaration declaration) {
        DauSoumiseEventPayload payload = DauSoumiseEventPayload.from(
                declaration.getPayloadJson(),
                declaration.getId(),
                declaration.getCorrelationId(),
                declaration.getDeclarantId(),
                declaration.getCustomsOfficeCode(),
                declaration.getReferenceNumber(),
                declaration.getStatus().name(),
                declaration.getCreatedAt());
        lastEvents.add(payload);
        log.info("[SIMULATION] Topic={} événement DAU soumise id={} correlationId={}",
                guceProperties.getKafka().getTopicDauSoumise(),
                declaration.getId(),
                declaration.getCorrelationId());
        log.debug("[SIMULATION] Payload complet: {}", payload);
    }

    /** Exposé pour tests ou inspection (optionnel). */
    public List<DauSoumiseEventPayload> snapshotLastEvents() {
        return List.copyOf(lastEvents);
    }
}
