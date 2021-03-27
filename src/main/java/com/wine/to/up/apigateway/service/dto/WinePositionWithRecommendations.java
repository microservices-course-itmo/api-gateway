package com.wine.to.up.apigateway.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class WinePositionWithRecommendations {
    @JsonProperty("wine_position")
    private WinePositionWithFavorites wine_position;

    @JsonProperty("recommendations")
    private List<WinePositionWithFavorites> recommendations;
}
