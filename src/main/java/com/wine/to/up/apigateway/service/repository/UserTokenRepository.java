package com.wine.to.up.apigateway.service.repository;

public interface UserTokenRepository {
    boolean containsToken(String token);

    void addToken(String token);

    void clearTokens();
}
