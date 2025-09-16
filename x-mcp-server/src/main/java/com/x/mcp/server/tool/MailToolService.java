package com.x.mcp.server.tool;

import com.x.mcp.server.annotation.ToolService;
import com.x.mcp.server.service.MailService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author : xuemingqi
 * @since : 2025/09/15 16:50
 */
@Slf4j
@ToolService
public class MailToolService {

    @Resource
    public MailService mailService;


    @Tool(name = "sendMail", description = "Send an email to one or more recipients")
    public void sendMail(@ToolParam(description = "Sender email address") String from,
                         @ToolParam(description = "Recipient email addresses") String[] tos,
                         @ToolParam(description = "Email subject") String subject,
                         @ToolParam(description = "Email content") String content) {
        mailService.sendMail(from, tos, subject, content);
    }
}
