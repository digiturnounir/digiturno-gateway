package com.unir.gateway.decorator;

import com.unir.gateway.model.GatewayRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;

/**
 * This class is a decorator for the GatewayRequest object for DELETE requests.
 * It extends the ServerHttpRequestDecorator class and overrides its methods to modify the request.
 */
@Slf4j
public class DeleteRequestDecorator extends ServerHttpRequestDecorator {

    private final GatewayRequest gatewayRequest;

    public DeleteRequestDecorator(GatewayRequest gatewayRequest) {
        super(gatewayRequest.getExchange().getRequest());
        this.gatewayRequest = gatewayRequest;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.DELETE;
    }

    @Override
    public URI getURI() {
        String query = null;
        if (gatewayRequest.getQueryParams() != null && !gatewayRequest.getQueryParams().isEmpty()) {
            StringBuilder queryBuilder = new StringBuilder();
            gatewayRequest.getQueryParams().forEach((key, value) -> {
                if (queryBuilder.length() > 0) {
                    queryBuilder.append("&");
                }
                queryBuilder.append(key).append("=").append(value);
            });
            query = queryBuilder.toString();
        }

        URI originalUri = (URI) gatewayRequest.getExchange().getAttributes().get(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        return UriComponentsBuilder.fromUri(originalUri)
                .replaceQuery(query)
                .build(true)
                .toUri();
    }

    @Override
    public HttpHeaders getHeaders() {
        return gatewayRequest.getHeaders();
    }

    @Override
    public Flux<DataBuffer> getBody() {
        // DELETE requests typically don't have a body
        return Flux.empty();
    }
}
