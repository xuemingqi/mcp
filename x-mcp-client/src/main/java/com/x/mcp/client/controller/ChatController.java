package com.x.mcp.client.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : xuemingqi
 * @since : 2025/09/08 13:52
 */
@RestController
public class ChatController {

    @Resource
    private DeepSeekChatModel deepSeekChatModel;

    @Resource
    private ToolCallbackProvider toolCallbackProvider;


    @Resource
    private ChatMemoryRepository chatMemoryRepository;

    @GetMapping("/chat")
    public String chat(@RequestParam("question") String question, @RequestParam("conversationId") String conversationId) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(100)
                .build();

//        HttpClientSseClientTransport server1 = HttpClientSseClientTransport.builder("http://127.0.0.1:18081")
//                .sseEndpoint("/sse?key=sk-e7030e17d1d64881a44a53b359af1644")
//                .build();
//        McpSyncClient client1 = McpClient.sync(server1).build();
//        client1.initialize();
//        client1.ping();
//
//        List<McpSyncClient> mcpClients = new ArrayList<>();
//        mcpClients.add(client1);
//
//        ToolCallbackProvider toolCallbackProvider = new SyncMcpToolCallbackProvider(mcpClients);

        ToolCallback[] tools = toolCallbackProvider.getToolCallbacks();
        for (ToolCallback tool : tools){
            tool.getToolDefinition().name();
        }
        ChatOptions chatOptions = DeepSeekChatOptions.builder()
                .toolCallbacks(tools)
                .build();

        UserMessage message = new UserMessage(question);
        chatMemory.add(conversationId, message);
        Prompt prompt = new Prompt(chatMemory.get(conversationId), chatOptions);

        return deepSeekChatModel.call(prompt).getResult().getOutput().getText();
    }



}
