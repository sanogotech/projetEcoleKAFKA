package com.guce.payment.messaging;

import java.util.Map;

public interface TopicPublisher {

    void publish(String topic, String key, Map<String, Object> payload);
}
