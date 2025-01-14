package com.angle.security_gateway.Config.Filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class LoggingFilterConfig {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilterConfig.class);

    @Bean
    @Order(0)
    public GlobalFilter loggingGlobalFilter() {
        return (exchange, chain) -> {
            logRequest(exchange.getRequest());
            return chain.filter(exchange);
        };
    }

    private void logRequest(ServerHttpRequest request) {
        boolean authHeader = request.getHeaders().containsKey("Authentication");
        logger.info("Incoming Request -> Method: {}, URI: {}, IP: {}, AUTH_HEADER_FOUND: {}",
                request.getMethod(),
                request.getURI(),
                request.getRemoteAddress(),
                authHeader
        );
    }

}
