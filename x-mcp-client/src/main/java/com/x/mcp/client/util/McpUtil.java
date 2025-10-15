package com.x.mcp.client.util;

import com.x.mcp.client.enums.AuthType;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author : xuemingqi
 * @since : 2025/10/15 13:48
 */
public class McpUtil {


    /**
     * 构建 WebClient.Builder 实例。
     * 可选地在请求头中添加认证信息（BASIC 或 BEARER）。
     *
     * @param url      基础请求地址（baseUrl）
     * @param isAuth   是否启用认证
     * @param authType 认证类型（BASIC / BEARER）
     * @param key      认证凭证（token 或用户名密码）
     * @return 构建完成的 {@link WebClient.Builder} 实例
     */
    public static WebClient.Builder getWebClient(String url, boolean isAuth, AuthType authType, String key) {
        WebClient.Builder webclientBuilder = WebClient.builder().baseUrl(url);
        if (isAuth) {
            if (AuthType.BASIC.equals(authType) || AuthType.BEARER.equals(authType)) {
                webclientBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, authType.getPrefix() + key);
            }
        }
        return webclientBuilder;
    }

    /**
     * 构建带有认证头的 HttpRequest.Builder。
     * 当认证类型为 BASIC 或 BEARER 时，将自动添加 Authorization 头。
     *
     * @param authType 认证类型（BASIC / BEARER）
     * @param key      认证凭证（token 或用户名密码）
     * @return 带认证头的 {@link HttpRequest.Builder}
     */
    public static HttpRequest.Builder getHttpRequest(AuthType authType, String key) {
        if (AuthType.BASIC.equals(authType) || AuthType.BEARER.equals(authType)) {
            return HttpRequest.newBuilder().header(HttpHeaders.AUTHORIZATION, authType.getPrefix() + key);
        }
        return HttpRequest.newBuilder();
    }

    /**
     * 构建基于 WebFlux 的 SSE（Server-Sent Events）客户端传输对象。
     * 该方法用于与基于 WebFlux 的服务器进行 SSE 通信。
     *
     * @param webClientBuilder WebClient 构建器（可包含认证或自定义配置）
     * @param sseEndpoint      SSE 接口路径（例如 "/sse"）
     * @return {@link WebFluxSseClientTransport} 实例
     */
    public static WebFluxSseClientTransport getWebFluxSseClientTransport(WebClient.Builder webClientBuilder, String sseEndpoint) {
        return WebFluxSseClientTransport.builder(webClientBuilder)
                .sseEndpoint(sseEndpoint)
                .build();
    }

    /**
     * 构建基于 WebFlux 的 SSE（Server-Sent Events）客户端传输对象。
     * 该方法用于与基于 WebFlux 的服务器进行 SSE 通信。
     *
     * @param url         基础请求地址（baseUrl）
     * @param sseEndpoint SSE 接口路径（例如 "/sse"）
     * @param isAuth      是否启用认证
     * @param authType    认证类型（BASIC / BEARER）
     * @param key         认证凭证（token 或用户名密码）
     * @return {@link WebFluxSseClientTransport} 实例
     */
    public static WebFluxSseClientTransport getWebFluxSseClientTransport(String url, String sseEndpoint, boolean isAuth, AuthType authType, String key) {
        return WebFluxSseClientTransport.builder(getWebClient(url, isAuth, authType, key))
                .sseEndpoint(sseEndpoint)
                .build();
    }

    /**
     * 构建基于 HttpClient 的 SSE 客户端传输对象。
     * 支持可选的认证请求构建器，用于添加 Authorization 等头部。
     *
     * @param url            SSE 服务端地址
     * @param isAuth         是否启用认证
     * @param sseEndpoint    SSE 接口路径（例如 "/sse"）
     * @param requestBuilder 自定义的 HttpRequest.Builder（可带认证头）
     * @return {@link HttpClientSseClientTransport} 实例
     */
    public static HttpClientSseClientTransport getHttpClientSseClientTransport(String url, String sseEndpoint, boolean isAuth, HttpRequest.Builder requestBuilder) {
        HttpClientSseClientTransport.Builder builder = HttpClientSseClientTransport.builder(url)
                .sseEndpoint(sseEndpoint);
        if (isAuth) {
            builder.requestBuilder(requestBuilder);
        }
        return builder.build();
    }

    /**
     * 构建基于 HttpClient 的 SSE 客户端传输对象。
     * 支持可选的认证请求构建器，用于添加 Authorization 等头部。
     *
     * @param url         基础请求地址（baseUrl）
     * @param sseEndpoint SSE 接口路径（例如 "/sse"）
     * @param isAuth      是否启用认证
     * @param authType    认证类型（BASIC / BEARER）
     * @param key         认证凭证（token 或用户名密码）
     * @return {@link HttpClientSseClientTransport} 实例
     */
    public static HttpClientSseClientTransport getHttpClientSseClientTransport(String url, String sseEndpoint, boolean isAuth, AuthType authType, String key) {
        HttpClientSseClientTransport.Builder builder = HttpClientSseClientTransport.builder(url)
                .sseEndpoint(sseEndpoint);
        if (isAuth) {
            builder.requestBuilder(getHttpRequest(authType, key));
        }
        return builder.build();
    }

    /**
     * 基于 WebFlux 构建并返回一个已初始化的 {@link McpSyncClient} 客户端实例。
     * 该方法内部通过 {@link WebFluxSseClientTransport} 建立 SSE（Server-Sent Events）通信通道，
     * 完成客户端构建、初始化（initialize）和健康检查（ping）。
     *
     * @param url         目标服务地址
     * @param sseEndpoint SSE 接口路径（例如：/sse/stream）
     * @param isAuth      是否启用认证
     * @param authType    认证类型（{@link AuthType#BASIC} 或 {@link AuthType#BEARER}）
     * @param key         认证凭证（例如 Token 或 Base64 编码的用户名密码）
     * @return 已初始化并可直接使用的 {@link McpSyncClient} 实例
     */
    public static McpSyncClient getWebFluxMcpSyncClient(String url, String sseEndpoint, boolean isAuth, AuthType authType, String key) {
        McpSyncClient client = McpClient.sync(getWebFluxSseClientTransport(url, sseEndpoint, isAuth, authType, key)).build();
        client.initialize();
        client.ping();
        return client;
    }

    /**
     * 基于 WebMvc（HttpClient）构建并返回一个已初始化的 {@link McpSyncClient} 客户端实例。
     * 该方法内部通过 {@link HttpClientSseClientTransport} 建立 SSE（Server-Sent Events）通信通道，
     * 完成客户端构建、初始化（initialize）和健康检查（ping）。
     * 适用于传统 Servlet 模型（阻塞 I/O）应用场景。
     *
     * @param url         目标服务地址
     * @param sseEndpoint SSE 接口路径（例如：/sse/stream）
     * @param isAuth      是否启用认证
     * @param authType    认证类型（{@link AuthType#BASIC} 或 {@link AuthType#BEARER}）
     * @param key         认证凭证（例如 Token 或 Base64 编码的用户名密码）
     * @return 已初始化并可直接使用的 {@link McpSyncClient} 实例
     */
    public static McpSyncClient getWebMvcMcpSyncClient(String url, String sseEndpoint, boolean isAuth, AuthType authType, String key) {
        McpSyncClient client = McpClient.sync(getHttpClientSseClientTransport(url, sseEndpoint, isAuth, authType, key)).build();
        client.initialize();
        client.ping();
        return client;
    }

    /**
     * 根据多个 SSE 客户端传输对象创建同步工具回调提供者。
     * 每个 {@link WebFluxSseClientTransport} 将被用来构建并初始化一个 {@link McpSyncClient}，
     * 所有客户端初始化并 ping 成功后，组成一个 {@link SyncMcpToolCallbackProvider}。
     *
     * @param transports WebFlux SSE 客户端传输列表
     * @return {@link ToolCallbackProvider} 实例，可用于注册或触发工具回调
     */
    public static SyncMcpToolCallbackProvider getToolCallbackProvider(List<McpClientTransport> transports) {
        List<McpSyncClient> mcpClients = new ArrayList<>();
        transports.forEach(t -> {
            try (McpSyncClient client = McpClient.sync(t).build()) {
                client.initialize();
                client.ping();
                mcpClients.add(client);
            }
        });
        return new SyncMcpToolCallbackProvider(mcpClients);
    }

    /**
     * 基于 WebFlux 实现的单例 MCP 工具回调提供者构建方法。
     * 使用指定的 URL 和 SSE 接口，构建一个 {@link McpSyncClient}，
     * 并通过 {@link SyncMcpToolCallbackProvider} 对外提供工具回调能力。
     *
     * @param url         目标服务地址
     * @param sseEndpoint SSE 接口路径（例如：/sse/stream）
     * @param isAuth      是否启用认证
     * @param authType    认证类型（{@link AuthType#BASIC} 或 {@link AuthType#BEARER}）
     * @param key         认证凭证（例如 Token 或 Base64 编码的用户名密码）
     * @return {@link SyncMcpToolCallbackProvider} 单例实例
     */
    public static SyncMcpToolCallbackProvider getSingletonWebFluxToolCallbackProvider(String url, String sseEndpoint, boolean isAuth, AuthType authType, String key) {
        McpSyncClient client = getWebFluxMcpSyncClient(url, sseEndpoint, isAuth, authType, key);
        return new SyncMcpToolCallbackProvider(Collections.singletonList(client));
    }

    /**
     * 基于 WebMvc（HttpClient）实现的单例 MCP 工具回调提供者构建方法。
     * 使用 {@link HttpClientSseClientTransport} 构建一个 {@link McpSyncClient}，
     * 并通过 {@link SyncMcpToolCallbackProvider} 统一封装返回。
     * 适用于基于传统 Servlet 模型的服务端 SSE 通信。
     *
     * @param url         目标服务地址
     * @param sseEndpoint SSE 接口路径（例如：/sse/stream）
     * @param isAuth      是否启用认证
     * @param authType    认证类型（{@link AuthType#BASIC} 或 {@link AuthType#BEARER}）
     * @param key         认证凭证（例如 Token 或 Base64 编码的用户名密码）
     * @return {@link SyncMcpToolCallbackProvider} 单例实例
     */
    public static SyncMcpToolCallbackProvider getSingletonWebMvcToolCallbackProvider(String url, String sseEndpoint, boolean isAuth, AuthType authType, String key) {
        McpSyncClient client = getWebMvcMcpSyncClient(url, sseEndpoint, isAuth, authType, key);
        return new SyncMcpToolCallbackProvider(Collections.singletonList(client));
    }

    /**
     * 调用 MCP 工具（Tool）并返回执行结果。
     * 该方法封装了 {@link McpSyncClient#callTool(McpSchema.CallToolRequest)} 调用逻辑，
     * 可通过指定工具名称和参数（arguments）发起一次同步工具调用请求。
     *
     * @param client    已初始化的 {@link McpSyncClient} 客户端实例
     * @param toolName  工具名称（对应 MCP 服务器注册的工具标识）
     * @param arguments 调用参数（以键值对形式传入，可为 null）
     * @return {@link McpSchema.CallToolResult} 工具执行结果对象，包含输出与状态信息
     */
    public static McpSchema.CallToolResult callTool(McpSyncClient client, String toolName, Map<String, Object> arguments) {
        return client.callTool(new McpSchema.CallToolRequest(toolName, arguments));
    }
}
