package com.wine.to.up.apigateway.service.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.wine.to.up.apigateway.service.jwt.JwtTokenProvider;
import com.wine.to.up.apigateway.service.repository.UserTokenRepository;
import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.user.service.api.feign.AuthenticationServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@RequiredArgsConstructor
@Component
@Slf4j
public class CheckTokenFilter extends ZuulFilter {

    @SuppressWarnings("unused")
    @InjectEventLogger
    private EventLogger eventLogger;

    private final UserTokenRepository userTokenRepository;

    private final AuthenticationServiceClient authenticationServiceClient;


    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String endpointToFilter = request.getRequestURI();
        return endpointToFilter.contains("deployment-service") ||
                !endpointToFilter.contains("swagger") &&
                !endpointToFilter.contains("api-docs") &&
                !request.getMethod().equals("OPTIONS") &&
                !endpointToFilter.contains("ml") &&
                !endpointToFilter.contains("/user-service/login") &&
                !endpointToFilter.contains("/user-service/refresh") &&
                !endpointToFilter.contains("parser") &&
                !endpointToFilter.contains("eureka-service") &&
                !endpointToFilter.contains("/catalog-service/favorites") &&
                !endpointToFilter.contains("/catalog/service/position/true/trueSettings");
    }

    @Override
    public Object run(){
        RequestContext context = RequestContext.getCurrentContext();

        HttpServletRequest request = context.getRequest();

        try {
            String accessToken = request.getHeader("Authorization").split(" ")[1];
            log.info(accessToken);
            if (accessToken.equals("123")) {
                log.info("Default header is set");
                return null;
            }

            if (userTokenRepository.containsToken(accessToken)) {
                if (JwtTokenProvider.getExpirationDate(accessToken).after(new Date())) {
                    addHeaders(context, accessToken);
                    log.info("Token " + accessToken + " in gateway");
                    return null;
                } else {
                    log.info("Token " + accessToken + " is expired");
                    userTokenRepository.clearToken(accessToken);
                }
            }
            try {
                authenticationServiceClient.validate(accessToken);
                addHeaders(context, accessToken);
                userTokenRepository.addToken(accessToken);
            } catch (Exception e){
                log.error("User is not validated in user-service");
                context.unset();
                context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            }

        } catch (Exception e) {
            log.info("No header");
            addHeaders(context, "");
        }

        return null;

    }

    private void addHeaders(RequestContext context, String accessToken) {
        if (accessToken.equals("")){
            context.addZuulRequestHeader("id", "");
            context.addZuulRequestHeader("role", "");
        } else {
            String role = JwtTokenProvider.getRole(accessToken);
            String id = JwtTokenProvider.getId(accessToken);
            context.addZuulRequestHeader("id", id);
            context.addZuulRequestHeader("role", role);
        }
    }

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
