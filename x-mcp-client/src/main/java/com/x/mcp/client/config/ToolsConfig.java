package com.x.mcp.client.config;

import com.x.mcp.client.properties.McpClientProperties;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import jakarta.annotation.Resource;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : xuemingqi
 * @since : 2025/09/25 15:51
 */
@Configuration
public class ToolsConfig {

    @Resource
    private McpClientProperties mcpClientProperties;

    @Bean
    public ToolCallbackProvider toolCallbackProvider() {
        List<McpSyncClient> mcpClients = new ArrayList<>();
        mcpClientProperties.getConnections().forEach((k, server) -> {
            HttpClientSseClientTransport sseClient = HttpClientSseClientTransport.builder(server.url())
                    .sseEndpoint(server.sseEndpoint())
                    .build();
            McpSyncClient client = McpClient.sync(sseClient).build();
            client.initialize();
            client.ping();
            mcpClients.add(client);
        });
        return new SyncMcpToolCallbackProvider(mcpClients);
    }
}
