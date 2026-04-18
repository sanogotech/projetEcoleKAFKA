package com.guce.notification.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnExpression("'${guce.mode}' == 'prod' || '${guce.mode}' == 'sim'")
public class KafkaTopicPublisher implements TopicPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaTopicPublisher.class);

    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    public KafkaTopicPublisher(KafkaTemplate<String, Map<String, Object>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String topic, String key, Map<String, Object> payload) {
        kafkaTemplate.send(topic, key, payload).whenComplete((r, e) -> {
            if (e != null) {
                log.error("Kafka publish failed topic={}", topic, e);
            }
        });
    }
}
