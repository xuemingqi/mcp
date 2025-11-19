package com.x.mcp.server.tool;

import com.x.mcp.server.service.MailService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

/**
 * @author : xuemingqi
 * @since : 2025/09/15 16:50
 */
@Slf4j
@Component
public class MailToolService {

    @Resource
    public MailService mailService;


    @McpTool(name = "sendMail", description = "Send an email to one or more recipients")
    public void sendMail(@McpToolParam(description = "Sender email address") String from,
                         @McpToolParam(description = "Recipient email addresses") String[] tos,
                         @McpToolParam(description = "Email subject") String subject,
                         @McpToolParam(description = "Email content") String content) {
        mailService.sendMail(from, tos, subject, content);
    }
}
