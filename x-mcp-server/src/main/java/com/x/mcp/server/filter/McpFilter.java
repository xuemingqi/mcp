package com.x.mcp.server.filter;

import com.x.framework.common.constants.CommonConstant;
import com.x.framework.common.exception.enums.ResponseCodeEnum;
import com.x.framework.common.response.ResultUtil;
import com.x.framework.common.util.IdUtil;
import com.x.framework.common.util.JsonUtil;
import com.x.framework.redis.util.RedisUtil;
import com.x.mcp.server.db.entity.Users;
import com.x.mcp.server.db.service.UsersIService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : xuemingqi
 * @since : 2025/03/26 14:39
 */
@Slf4j
@Component
public class McpFilter implements WebFilter {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private UsersIService usersIService;

    private static final Pattern SESSION_ID_PATTERN = Pattern.compile("sessionId=([0-9a-fA-F\\-]{36})");


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        try {
            String traceId = IdUtil.randomAlphanumeric(10);
            MDC.put(CommonConstant.TRACE_ID, traceId);
            ServerHttpRequest originalRequest = exchange.getRequest();
            String path = originalRequest.getPath().value();
            MultiValueMap<String, String> queryParams = originalRequest.getQueryParams();

            log.info("URL: {}", originalRequest.getURI());
            log.info("Method: {}", originalRequest.getMethod());
            log.info("Headers: {}", originalRequest.getHeaders());
            log.info("Query Params: {}", queryParams);
            Users user = null;
            if ("/sse".equals(path)) {
                String key = queryParams.getFirst(CommonConstant.KEY);
                log.info("Key: {}", key);
                user = usersIService.lambdaQuery()
                        .eq(Users::getToken, key)
                        .one();
                if (user == null) {
                    return unauthorized(exchange.getResponse());
                }
            } else {
                String sid = queryParams.getFirst(CommonConstant.SESSION_ID);
                Long id = redisUtil.get(sid);
                log.info("Id: {}", id);
                if (sid == null || id == null) {
                    return unauthorized(exchange.getResponse());
                }
            }

            Flux<DataBuffer> loggedBody = originalRequest.getBody()
                    .doOnNext(dataBuffer -> {
                        try (DataBuffer.ByteBufferIterator iterator = dataBuffer.readableByteBuffers()) {
                            while (iterator.hasNext()) {
                                MDC.put(CommonConstant.TRACE_ID, traceId);
                                ByteBuffer byteBuffer = iterator.next();
                                byte[] bytes = new byte[byteBuffer.remaining()];
                                byteBuffer.get(bytes);
                                String chunk = new String(bytes, StandardCharsets.UTF_8);
                                log.info("Request: {}", chunk);
                            }
                        }
                    });
            ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(originalRequest) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return loggedBody;
                }
            };
            ServerHttpResponseDecorator decoratedResponse = getServerHttpResponseDecorator(exchange, path, user);
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(decoratedRequest)
                    .response(decoratedResponse)
                    .build();

            return chain.filter(mutatedExchange);
        } finally {
            MDC.clear();
        }
    }

    private ServerHttpResponseDecorator getServerHttpResponseDecorator(ServerWebExchange exchange, String requestPath, Users user) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                Flux<DataBuffer> flattened = Flux.from(body)
                        .flatMapSequential(p -> ((Flux<DataBuffer>) p)
                                .doOnNext(dataBuffer -> {
                                    ByteBuffer byteBuffer = dataBuffer.asByteBuffer().duplicate();
                                    byte[] bytes = new byte[byteBuffer.remaining()];
                                    byteBuffer.get(bytes);
                                    String chunk = new String(bytes, StandardCharsets.UTF_8);
                                    log.info("Response: {}", chunk);
                                    if ("/sse".equals(requestPath)) {
                                        extractAndRegisterSession(chunk);
                                    }
                                }));
                return super.writeWith(flattened);
            }

            private void extractAndRegisterSession(String sseEvent) {
                Matcher m = SESSION_ID_PATTERN.matcher(sseEvent);
                if (m.find() && user != null) {
                    String sessionId = m.group(1);
                    redisUtil.set(sessionId, user.getId());
                }
            }
        };
    }

    private static Mono<Void> unauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = JsonUtil.toJsonStr(ResultUtil.buildResultError(ResponseCodeEnum.UNAUTHORIZED)).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
