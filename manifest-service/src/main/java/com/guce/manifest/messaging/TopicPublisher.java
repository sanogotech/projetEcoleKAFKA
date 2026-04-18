package com.guce.manifest.messaging;

import java.util.Map;

public interface TopicPublisher {

    void publish(String topic, String key, Map<String, Object> payload);
}
