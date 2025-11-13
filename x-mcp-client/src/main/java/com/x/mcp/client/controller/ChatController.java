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


    @GetMapping(value = "/chat")
    public Flux<String> chat(@RequestParam("question") String question, @RequestParam("conversationId") String conversationId) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(100)
                .build();

        ToolCallback[] tools = toolCallbackProvider.getToolCallbacks();

        ChatOptions chatOptions = OpenAiChatOptions.builder()
                .toolCallbacks(tools)
                .build();

        UserMessage message = new UserMessage(question);
        chatMemory.add(conversationId, message);
        Prompt prompt = new Prompt(chatMemory.get(conversationId), chatOptions);

        return openAiChatModel.stream(prompt).mapNotNull(response -> response.getResult().getOutput().getText());
    }
}
