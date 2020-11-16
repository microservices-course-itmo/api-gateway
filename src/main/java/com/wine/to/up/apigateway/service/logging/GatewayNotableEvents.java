package com.wine.to.up.apigateway.service.logging;

import com.wine.to.up.commonlib.logging.NotableEvent;

public enum GatewayNotableEvents implements NotableEvent {
    AUTH_ERROR("Authentication failed: {}"),
    DEFAULT_HEADER("Header is set to default"),
    TOKEN_EXPIRED("Access token is expired: {}");


    private final String template;

    GatewayNotableEvents(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    @Override
    public String getName() {
        return name();
    }
}
