package com.unir.gateway.decorator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unir.gateway.model.GatewayRequest;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;

/**
 * This class is a decorator for the GatewayRequest object for PUT requests.
 * It extends the ServerHttpRequestDecorator class and overrides its methods to modify the request.
 * It uses the ObjectMapper to convert the body of the GatewayRequest object into bytes.
 */
@Slf4j
public class PutRequestDecorator extends ServerHttpRequestDecorator {

    private final GatewayRequest gatewayRequest;
    private final ObjectMapper objectMapper;

    public PutRequestDecorator(GatewayRequest gatewayRequest, ObjectMapper objectMapper) {
        super(gatewayRequest.getExchange().getRequest());
        this.gatewayRequest = gatewayRequest;
        this.objectMapper = objectMapper;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
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
    @SneakyThrows
    public Flux<DataBuffer> getBody() {
        if (gatewayRequest.getBody() != null) {
            DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
            String body = objectMapper.writeValueAsString(gatewayRequest.getBody());
            DataBuffer buffer = bufferFactory.wrap(body.getBytes());
            log.info("PUT Request body: {}", body);
            return Flux.just(buffer);
        } else {
            return Flux.empty();
        }
    }
}
