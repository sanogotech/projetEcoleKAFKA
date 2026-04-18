package com.guce.declaration.application.port.out;

import com.guce.declaration.domain.Declaration;

/**
 * Publication de l'événement métier « DAU soumise » vers Kafka (ou simulation).
 */
public interface DauSoumisePublisher {

    void publish(Declaration declaration);
}
