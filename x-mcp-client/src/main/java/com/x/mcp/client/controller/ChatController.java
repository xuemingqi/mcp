package com.x.mcp.client.controller;

import jakarta.annotation.Resource;
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
    private ToolCallbackProvider provider;

    @GetMapping("/chat")
    public String chat(@RequestParam("question") String question) {
        ToolCallback[] tools = provider.getToolCallbacks();
        ChatOptions chatOptions = DeepSeekChatOptions.builder()
                .toolCallbacks(tools)
                .build();
        Prompt prompt = new Prompt(question, chatOptions);
        return deepSeekChatModel.call(prompt).getResult().getOutput().getText();
    }

}
