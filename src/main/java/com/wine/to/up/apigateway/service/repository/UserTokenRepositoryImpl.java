package com.wine.to.up.apigateway.service.repository;


import com.wine.to.up.apigateway.service.jwt.JwtTokenProvider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Getter
@Setter
public class UserTokenRepositoryImpl implements UserTokenRepository {
    private Set<String> tokens;

    public UserTokenRepositoryImpl() {
        this.tokens = new HashSet<>();
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
    public void clearTokens(){
        tokens.removeIf(token -> JwtTokenProvider.getExpirationDate(token).before(new Date()));
    }
}
