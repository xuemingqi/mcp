package com.x.mcp.client.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : xuemingqi
 * @since : 2025/09/25 17:09
 */
@Getter
@Component
@ConfigurationProperties("mcp.sse")
public class McpClientProperties {

    private final Map<String, SseParameters> connections = new HashMap<>();

    public record SseParameters(String url, String sseEndpoint) {
    }
}
