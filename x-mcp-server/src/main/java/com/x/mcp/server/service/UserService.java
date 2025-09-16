package com.x.mcp.server.service;

import com.x.mcp.server.db.entity.User;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * @author : xuemingqi
 * @since : 2025/09/16 14:04
 */
public interface UserService {

    /**
     * 获取用户列表
     *
     * @return 用户列表
     */
    @Async("threadPoolTaskExecutor")
    List<User> getUserList();
}
