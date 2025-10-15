package com.x.mcp.client.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.*;

/**
 * @author : xuemingqi
 * @since : 2025/09/08 13:52
 */
@RestController
public class ChatController {

    @Resource
    private OpenAiChatModel openAiChatModel;

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    @Resource
    private ChatMemoryRepository chatMemoryRepository;


    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestParam("question") String question, @RequestParam("conversationId") String conversationId) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(100)
                .build();

//        ToolCallbackProvider toolCallbackProvider = McpUtil.getSingletonWebFluxToolCallbackProvider("http://127.0.0.1:8081/",
//                "sse?key=sk-e7030e17d1d64881a44a53b359af1644", false, AuthType.CUSTOM, null);

        ToolCallback[] tools = toolCallbackProvider.getToolCallbacks();
        for (ToolCallback tool : tools) {
            tool.getToolDefinition().name();
        }
        ChatOptions chatOptions = OpenAiChatOptions.builder()
                .model("deepseek-chat")
                //.httpHeaders(Map.of("LM-Request-TraceDataId", "1760428570706-0d1247__9fa987519fd7458b9c97e08ebf0bc9ab__c6c5f5aba2564dd2992752552069af81"))
                .toolCallbacks(tools)
                .build();

        UserMessage message = new UserMessage(question);
        chatMemory.add(conversationId, message);
        Prompt prompt = new Prompt(chatMemory.get(conversationId), chatOptions);

        return openAiChatModel.stream(prompt).mapNotNull(response -> response.getResult().getOutput().getText());
    }
}
