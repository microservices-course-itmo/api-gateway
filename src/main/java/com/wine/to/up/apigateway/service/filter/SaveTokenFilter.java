package com.wine.to.up.apigateway.service.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.wine.to.up.apigateway.service.jwt.JwtTokenProvider;
import com.wine.to.up.apigateway.service.service.TokenService;
import com.wine.to.up.user.service.api.dto.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

@RequiredArgsConstructor
@Component
public class SaveTokenFilter extends ZuulFilter {
    private final TokenService tokenService;


    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        /*String endpointToFilter = RequestContext.getCurrentContext().getRequest().getRequestURI();
        endpointToFilter = endpointToFilter.substring(0, endpointToFilter.indexOf("/", 1));
        return "/user-service".equals(endpointToFilter);*/
        return false;
    }

    @SneakyThrows
    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();

        try (final InputStream responseDataStream = context.getResponseDataStream()) {

            if(responseDataStream == null) {
                return null;
            }

            String responseData = CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8"));

            context.setResponseBody(responseData);

            ObjectMapper objectMapper = new ObjectMapper();
            AuthenticationResponse userServiceResponse = objectMapper
                    .readValue(context.getResponseBody(), AuthenticationResponse.class);

            System.out.println(userServiceResponse.getAccessToken());

            tokenService.addToken(userServiceResponse.getAccessToken());

        }
        catch (Exception e) {
            throw new ZuulException(e, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }

        //TODO: remove return
        return null;
    }
}
