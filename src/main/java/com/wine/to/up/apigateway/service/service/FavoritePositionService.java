package com.wine.to.up.apigateway.service.service;

import com.wine.to.up.apigateway.service.dto.WinePositionWithFavorites;
import com.wine.to.up.catalog.service.api.dto.WinePositionTrueResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoritePositionService {
    public List<WinePositionWithFavorites> convertWinePositions(
            List<WinePositionTrueResponse> winePositionTrueResponses,
            Set<String> favoriteIds){
        return winePositionTrueResponses.stream().map(response -> new WinePositionWithFavorites(
                response.getWine_position_id(),
                response.getShop(),
                response.getWineTrueResponse(),
                response.getPrice(),
                response.getActual_price(),
                response.getLink_to_wine(),
                response.getVolume(),
                response.getDescription(),
                response.getGastronomy(),
                response.getImage(),
                response.getCountry(),
                favoriteIds.contains(response.getWine_position_id()))).collect(Collectors.toList());
    }

    public List<WinePositionWithFavorites> getFavorites(List<WinePositionWithFavorites> positions){
        return positions.stream().filter(WinePositionWithFavorites::isLiked).collect(Collectors.toList());
    }
}
