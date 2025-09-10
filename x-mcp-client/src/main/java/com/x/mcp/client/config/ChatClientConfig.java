//package com.x.mcp.client.config;
//
//import org.springframework.ai.chat.memory.ChatMemory;
//import org.springframework.ai.deepseek.DeepSeekChatModel;
//import org.springframework.ai.deepseek.DeepSeekChatOptions;
//import org.springframework.ai.deepseek.api.DeepSeekApi;
//import org.springframework.ai.tool.ToolCallbackProvider;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author : xuemingqi
// * @since : 2025/09/08 13:33
// */
//@Configuration
//public class ChatClientConfig {
//
//    @Bean
//    public DeepSeekChatModel deepSeekChatModel(ChatMemory chatMemory, ToolCallbackProvider tools) {
//        return DeepSeekChatModel.builder()
//                .deepSeekApi(DeepSeekApi.builder()
//                        .apiKey("sk-91c2a7c3a5914a5d85ba9e219b289e96")
//                        .build())
//                .defaultOptions(DeepSeekChatOptions.builder()
//                        .toolCallbacks(tools.getToolCallbacks())
//                        .build())
//                .build();
//    }
//}
