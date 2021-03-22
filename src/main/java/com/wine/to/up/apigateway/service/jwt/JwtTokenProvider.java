package com.wine.to.up.apigateway.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    public static Date getExpirationDate(String token) {
        return getTokenBody(token).getExpiration();
    }

    public static String getRole(String token){
        return (String) getTokenBody(token).get("role");
    }

    public static String getId(String token){
        return (String) getTokenBody(token).get("id");
    }

    public static Claims getTokenBody(String token){
        String secret = Base64.getEncoder().encodeToString("jwtsecret".getBytes());
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}
