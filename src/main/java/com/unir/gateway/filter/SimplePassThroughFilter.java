package com.unir.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Simple pass-through filter that allows requests to go directly to services
 * without translation when the request doesn't need transformation.
 */
@Component
@Slf4j
public class SimplePassThroughFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Log the request for debugging
        log.info("Routing request: {} {} to service", 
                exchange.getRequest().getMethod(), 
                exchange.getRequest().getPath());
        
        // Simply pass the request through without modification
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Set lower order to run before the RequestTranslationFilter
        return -1;
    }
}
