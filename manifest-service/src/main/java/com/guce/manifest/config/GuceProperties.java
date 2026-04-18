package com.guce.manifest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "guce")
public class GuceProperties {

    private String mode = "dev";

    private final Kafka kafka = new Kafka();

    @Data
    public static class Kafka {
        private String topicManifesteRecu = "manifeste.recu";
    }
}
