package com.smn.restapigenerator.service.codegen;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Map;
import java.util.HashMap;

@Configuration
@ConfigurationProperties(prefix = "codegen")
public class ConfigProperties {

    // Name must match the key in YAML (config)
    private Map<String, Object> config = new HashMap<>();

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
}
