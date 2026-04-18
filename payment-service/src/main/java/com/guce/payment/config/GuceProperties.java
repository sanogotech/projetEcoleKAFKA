package com.guce.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "guce")
public class GuceProperties {

    private String mode = "dev";

    private final Kafka kafka = new Kafka();

    @Data
    public static class Kafka {
        private String topicDauTaxee = "dau.taxee";
        private String topicPaiementConfirme = "paiement.confirme";
    }
}
