package com.guce.declaration.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "guce")
public class GuceProperties {

    /**
     * dev = simulation locale (sans Kafka) ; prod / sim = publication Kafka réelle.
     */
    @NotBlank
    private String mode = "dev";

    private final Kafka kafka = new Kafka();

    @Data
    public static class Kafka {
        @NotBlank
        private String topicDauSoumise = "dau.soumise";
    }
}
