package com.guce.notification.api;

import com.guce.notification.api.dto.NotificationDispatchRequest;
import com.guce.notification.api.dto.NotificationDispatchResponse;
import com.guce.notification.config.GuceProperties;
import com.guce.notification.domain.NotificationDispatch;
import com.guce.notification.domain.NotificationDispatchRepository;
import com.guce.notification.messaging.TopicPublisher;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications")
public class NotificationController {

    private final NotificationDispatchRepository repository;
    private final TopicPublisher topicPublisher;
    private final GuceProperties guceProperties;

    public NotificationController(
            NotificationDispatchRepository repository,
            TopicPublisher topicPublisher,
            GuceProperties guceProperties) {
        this.repository = repository;
        this.topicPublisher = topicPublisher;
        this.guceProperties = guceProperties;
    }

    @PostMapping("/dispatch")
    @Operation(summary = "Demander l'envoi d'une notification", description = "Publie sur notification.demande")
    public ResponseEntity<NotificationDispatchResponse> dispatch(@Valid @RequestBody NotificationDispatchRequest request) {
        NotificationDispatch n = new NotificationDispatch();
        n.setId(UUID.randomUUID());
        n.setChannel(request.channel());
        n.setTarget(request.target());
        n.setBody(request.body());
        n.setCreatedAt(Instant.now());
        NotificationDispatch saved = repository.save(n);

        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventType", "NOTIFICATION_DEMANDE");
        event.put("dispatchId", saved.getId().toString());
        event.put("channel", saved.getChannel().name());
        event.put("target", saved.getTarget());
        event.put("occurredAt", saved.getCreatedAt().toString());
        topicPublisher.publish(
                guceProperties.getKafka().getTopicNotificationDemande(),
                saved.getId().toString(),
                event);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new NotificationDispatchResponse(saved.getId(), saved.getChannel().name(), saved.getTarget(), saved.getCreatedAt()));
    }
}
