package com.wine.to.up.apigateway.service.controller;

import com.netflix.zuul.context.RequestContext;
import com.wine.to.up.apigateway.service.dto.WinePositionWithFavorites;
import com.wine.to.up.apigateway.service.dto.WinePositionWithRecommendations;
import com.wine.to.up.apigateway.service.jwt.JwtTokenProvider;
import com.wine.to.up.apigateway.service.service.FavoritePositionService;
import com.wine.to.up.catalog.service.api.dto.WinePositionTrueResponse;
import com.wine.to.up.catalog.service.api.feign.FavoriteWinePositionsClient;
import com.wine.to.up.catalog.service.api.feign.WinePositionClient;
import com.wine.to.up.description.ml.api.feign.WineRecommendationServiceClient;
import com.wine.to.up.ml2.api.dto.RecommendationResponse;
import com.wine.to.up.ml2.api.feign.ML2WineRecommendationServiceClient;
import com.wine.to.up.user.service.api.dto.ItemDto;
import com.wine.to.up.user.service.api.feign.FavoritesServiceClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
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

    private final WineRecommendationServiceClient wineRecommendationServiceClient;

    /*private final ML2WineRecommendationServiceClient ml2WineRecommendationServiceClient;*/


    @ApiOperation(value = "Get favourites wine positions",
            nickname = "getFavouritesPositions",
            tags = {"favorite-positions-controller",})
    @GetMapping("/favorites")
    public List<WinePositionTrueResponse> getFavourites() {
        log.info("Got request for favorite positions");
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        setHeaders();

        String accessToken = request.getHeader("Authorization").split(" ")[1];

        List<String> ids = new ArrayList<>(getFavoriteIds(accessToken));

        if (ids.isEmpty()) return new ArrayList<>();

        Map<String, List<String>> query = new HashMap<>();
        query.put("favouritePosition", ids);
        return favoriteWinePositionsClient.getFavourites(query);
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
        String accessToken = "";
        try {
            accessToken = request.getHeader("Authorization").split(" ")[1];
        } catch (Exception e) {
            log.info("No header");
        }

        if (accessToken.equals("123") || accessToken.equals("")) {
            List<WinePositionTrueResponse> positions = getWinePositionTrueResponses(page, amount, sortByPair, filterBy);
            setHeaders();
            return favoritePositionService.convertWinePositions(positions, new HashSet<>());
        }

        Set<String> ids = getFavoriteIds(accessToken);
        List<WinePositionTrueResponse> positions = getWinePositionTrueResponses(page, amount, sortByPair, filterBy);

        log.info("Wine positions: " + positions.size());

        setHeaders();

        return favoritePositionService.convertWinePositions(positions, ids);
    }

    @GetMapping("/position/true/byId/{id}")
    public WinePositionWithFavorites getWineById(@Valid @PathVariable(name = "id") String winePositionId) {
        log.info("Got request for positions by id");
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String accessToken = "";
        try {
            accessToken = request.getHeader("Authorization").split(" ")[1];
        } catch (Exception e) {
            log.info("No header");
        }

        WinePositionTrueResponse response = favoriteWinePositionsClient.getPositionById(winePositionId);

        setHeaders();

        if (accessToken.equals("123") || accessToken.equals("")) {
            setHeaders();
            return favoritePositionService.getPosition(response, new HashSet<>());
        }

        Set<String> ids = getFavoriteIds(accessToken);
        return favoritePositionService.getPosition(response, ids);
    }

    @GetMapping("/rec/true/byId/{id}")
    public WinePositionWithRecommendations getWineWithRecommendations(@Valid @PathVariable(name = "id") String winePositionId) {
        log.info("Got request for positions by id");
        List<String> recommendationIds = wineRecommendationServiceClient.recommend(winePositionId);
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String accessToken = "";
        try {
            accessToken = request.getHeader("Authorization").split(" ")[1];
        } catch (Exception e) {
            log.info("No header");
        }

        WinePositionTrueResponse response = favoriteWinePositionsClient.getPositionById(winePositionId);

        List<WinePositionWithFavorites> positions = new ArrayList<>();

        Set<String> ids = new HashSet<>();

        if (!accessToken.equals("123") && !accessToken.equals("")) {
            ids = getFavoriteIds(accessToken);
        }

        Set<String> finalIds = ids;
        recommendationIds.forEach(id -> {
            WinePositionTrueResponse resp = favoriteWinePositionsClient.getPositionById(id);
            positions.add(favoritePositionService.getPosition(resp, finalIds));
        });

        setHeaders();
        return new WinePositionWithRecommendations(favoritePositionService.getPosition(response, finalIds), positions);
    }
/*
    @GetMapping("/rec1/true/byId/{id}")
    public WinePositionWithRecommendations getWineWithRecommendations2(@Valid @PathVariable(name = "id") String winePositionId) {
        log.info("Got request for positions by id");

        List<String> recommendationIds = ml2WineRecommendationServiceClient.recommend(0, 10).getContent();
        //List<String> recommendationIds = wineRecommendationServiceClient.recommend(winePositionId);
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String accessToken = "";
        try {
            accessToken = request.getHeader("Authorization").split(" ")[1];
        } catch (Exception e) {
            log.info("No header");
        }

        WinePositionTrueResponse response = favoriteWinePositionsClient.getPositionById(winePositionId);

        List<WinePositionWithFavorites> positions = new ArrayList<>();

        Set<String> ids = new HashSet<>();

        if (!accessToken.equals("123") && !accessToken.equals("")) {
            ids = getFavoriteIds(accessToken);
        }

        Set<String> finalIds = ids;
        recommendationIds.forEach(id -> {
            WinePositionTrueResponse resp = favoriteWinePositionsClient.getPositionById(id);
            positions.add(favoritePositionService.getPosition(resp, finalIds));
        });

        setHeaders();
        return new WinePositionWithRecommendations(favoritePositionService.getPosition(response, finalIds), positions);
    }*/

    private Set<String> getFavoriteIds(String accessToken) {
        String id = JwtTokenProvider.getId(accessToken);
        String role = JwtTokenProvider.getRole(accessToken);

        List<ItemDto> itemDtos = favoritesServiceClient.findUsersFavorites(id, role);
        return itemDtos.stream().map(ItemDto::getId).collect(Collectors.toSet());
    }

    private List<WinePositionTrueResponse> getWinePositionTrueResponses(String page, String amount, List<String> sortByPair, String filterBy) {
        Map<String, List<String>> query = new HashMap<>();
        List<String> amountList  = new ArrayList<>();
        amountList.add(amount);
        List<String> pageList  = new ArrayList<>();
        pageList.add(page);
        List<String> filterByList  = new ArrayList<>();
        filterByList.add(filterBy);
        query.put("sortByPair", sortByPair);
        query.put("amount", amountList);
        query.put("page", pageList);
        query.put("filterBy", filterByList);
        List<WinePositionTrueResponse> positions = winePositionClient.getAllWinePositionsTrue(query);
        return positions;
    }

    private void setHeaders() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletResponse servletResponse = context.getResponse();
        servletResponse.addHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        servletResponse.addHeader("Pragma", "no-cache");
        servletResponse.addHeader("Expires", "0");
    }

}
