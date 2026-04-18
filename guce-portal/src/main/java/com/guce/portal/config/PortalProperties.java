package com.guce.portal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "guce.services")
public class PortalProperties {

    private String declaration = "http://localhost:8080";
    private String manifest = "http://localhost:8081";
    private String authorization = "http://localhost:8082";
    private String payment = "http://localhost:8083";
    private String inspection = "http://localhost:8084";
    private String pcs = "http://localhost:8085";
    private String reference = "http://localhost:8086";
    private String operator = "http://localhost:8087";
    private String notification = "http://localhost:8088";
    private String audit = "http://localhost:8089";
}
