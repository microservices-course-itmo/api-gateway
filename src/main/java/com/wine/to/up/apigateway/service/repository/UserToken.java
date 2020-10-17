package com.wine.to.up.apigateway.service.repository;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.Set;

@RequiredArgsConstructor
@Getter
@Setter
@Repository
public class UserToken {
    private Set<String> tokens;
}
