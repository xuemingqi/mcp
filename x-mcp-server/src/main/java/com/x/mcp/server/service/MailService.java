package com.x.mcp.server.service;

import org.springframework.scheduling.annotation.Async;

/**
 * @author : xuemingqi
 * @since : 2025/03/26 16:49
 */
public interface MailService {

    /**
     * 发送邮件
     *
     * @param from    发件人邮箱地址
     * @param tos     收件人邮箱地址数组
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    @Async("threadPoolTaskExecutor")
    void sendMail(String from, String[] tos, String subject, String content);

}
