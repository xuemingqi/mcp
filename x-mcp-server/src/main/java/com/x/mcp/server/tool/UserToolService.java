package com.x.mcp.server.tool;

import com.x.mcp.server.db.entity.User;
import com.x.mcp.server.service.UserService;
import jakarta.annotation.Resource;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : xuemingqi
 * @since : 2025/03/26 16:55
 */
@Component
public class UserToolService {

    @Resource
    private UserService userService;

    @McpTool(name = "getUserList", description = "Get user list")
    public List<User> getUserList() {
        return userService.getUserList();
    }
}
