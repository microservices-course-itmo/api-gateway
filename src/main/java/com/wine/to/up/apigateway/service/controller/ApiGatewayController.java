package com.wine.to.up.apigateway.service.controller;

import com.netflix.zuul.context.RequestContext;
import com.wine.to.up.apigateway.service.dto.WinePositionWithFavorites;
import com.wine.to.up.apigateway.service.jwt.JwtTokenProvider;
import com.wine.to.up.apigateway.service.service.FavoritePositionService;
import com.wine.to.up.catalog.service.api.dto.WinePositionTrueResponse;
import com.wine.to.up.catalog.service.api.feign.FavoriteWinePositionsClient;
import com.wine.to.up.catalog.service.api.feign.WinePositionClient;
import com.wine.to.up.user.service.api.dto.ItemDto;
import com.wine.to.up.user.service.api.feign.FavoritesServiceClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/catalog-service")
@Validated
@Slf4j
@Api(value = "ApiGatewayController")
public class ApiGatewayController {


    private final FavoritesServiceClient favoritesServiceClient;

    private final FavoriteWinePositionsClient favoriteWinePositionsClient;

    private final WinePositionClient winePositionClient;

    private final FavoritePositionService favoritePositionService;


    @ApiOperation(value = "Get favourites wine positions",
            nickname = "getFavouritesPositions",
            tags = {"favorite-positions-controller",})
    @GetMapping("/favorites")
    public List<WinePositionTrueResponse> getFavourites() {
        log.info("Got request for favorite positions");
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String accessToken = request.getHeader("Authorization").split(" ")[1];

        String id = JwtTokenProvider.getId(accessToken);
        String role = JwtTokenProvider.getRole(accessToken);

        List<ItemDto> itemDtos = favoritesServiceClient.findUsersFavorites(id, role);

        log.info("Favorite positions amount: " + itemDtos.size());
        List<String> ids = itemDtos.stream().map(ItemDto::getId).collect(Collectors.toList());

        return favoriteWinePositionsClient.getFavourites(ids);
    }

    @ApiOperation(value = "Get favourites wine positions",
            nickname = "getFavouritesPositions",
            tags = {"favorite-positions-controller",})
    @GetMapping("/position/true/trueSettings")
    public List<WinePositionWithFavorites> getWinePositions(@RequestParam(required = false) String page,
                                                            @RequestParam(required = false) String amount,
                                                            @RequestParam(required = false) List<String> sortByPair,
                                                            @RequestParam(required = false) String filterBy) {
        log.info("Got request for positions with settings");
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String accessToken = request.getHeader("Authorization").split(" ")[1];

        String id = JwtTokenProvider.getId(accessToken);
        String role = JwtTokenProvider.getRole(accessToken);

        List<ItemDto> itemDtos = favoritesServiceClient.findUsersFavorites(id, role);
        Set<String> ids = itemDtos.stream().map(ItemDto::getId).collect(Collectors.toSet());
        List<WinePositionTrueResponse> positions = winePositionClient.getAllWinePositionsTrue(page, amount, sortByPair, filterBy);

        log.info("Wine positions: " + positions.size());

        return favoritePositionService.convertWinePositions(positions, ids);
    }

}
