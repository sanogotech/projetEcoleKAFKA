package com.guce.notification.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "guce.mode", havingValue = "dev", matchIfMissing = true)
public class SimulationTopicPublisher implements TopicPublisher {

    private static final Logger log = LoggerFactory.getLogger(SimulationTopicPublisher.class);

    @Override
    public void publish(String topic, String key, Map<String, Object> payload) {
        log.info("[SIMULATION] topic={} key={} payload={}", topic, key, payload);
    }
}
