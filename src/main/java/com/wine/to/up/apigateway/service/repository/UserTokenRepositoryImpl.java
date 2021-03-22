package com.wine.to.up.apigateway.service.repository;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Getter
@Setter
public class UserTokenRepositoryImpl implements UserTokenRepository {
    private Set<String> tokens;

    public UserTokenRepositoryImpl() {
        this.tokens = ConcurrentHashMap.newKeySet();
    }

    @Override
    public boolean containsToken(String token) {
        return tokens.contains(token);
    }

    @Override
    public void addToken(String token) {
        tokens.add(token);
    }

    @Override
    public void clearToken(String token){
        tokens.remove(token);
    }
}
