package com.febfes.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "custom-headers")
public class CustomHeadersConfig {

    private String initUri;
    private String userRole;
    private String username;

    public String getInitUri() {
        return initUri;
    }

    public void setInitUri(String initUri) {
        this.initUri = initUri;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}