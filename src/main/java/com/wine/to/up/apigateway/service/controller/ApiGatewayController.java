package com.wine.to.up.apigateway.service.controller;

import com.netflix.zuul.context.RequestContext;
import com.wine.to.up.catalog.service.api.dto.WinePositionTrueResponse;
import com.wine.to.up.catalog.service.api.feign.FavoriteWinePositionsClient;
import com.wine.to.up.catalog.service.api.service.FavoriteWinePositionsService;
import com.wine.to.up.user.service.api.dto.ItemDto;
import com.wine.to.up.user.service.api.feign.FavoritesServiceClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/catalog")
@Validated
@Slf4j
@Api(value = "ApiGatewayController")
public class ApiGatewayController {


    private final FavoritesServiceClient favoritesServiceClient;

    private final FavoriteWinePositionsClient favoriteWinePositionsClient;


    @ApiOperation(value = "Get favourites wine positions",
            nickname = "getFavouritesPositions",
            tags = {"favorite-positions-controller",})
    @GetMapping("/favorites")
    public List<WinePositionTrueResponse> getFavourites() {
        log.info("Got request for favorite positions");
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String accessToken = request.getHeader("Authorization").split(" ")[1];

        List<ItemDto> itemDtos = favoritesServiceClient.findUsersFavorites(accessToken);
        List<String> ids = itemDtos.stream().map(ItemDto::getId).collect(Collectors.toList());
        return favoriteWinePositionsClient.getFavourites(ids);
    }


}
