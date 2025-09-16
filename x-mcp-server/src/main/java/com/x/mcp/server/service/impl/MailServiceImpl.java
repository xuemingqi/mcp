package com.x.mcp.server.service.impl;

import com.x.mcp.server.service.MailService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author : xuemingqi
 * @since : 2025/03/26 16:55
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {

    @Override
    public void sendMail(String from, String[] tos, String subject, String content) {

    }
}
