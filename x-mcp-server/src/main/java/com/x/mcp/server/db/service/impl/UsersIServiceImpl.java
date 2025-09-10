package com.x.mcp.server.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.x.mcp.server.db.entity.Users;
import com.x.mcp.server.db.mapper.UsersMapper;
import com.x.mcp.server.db.service.UsersIService;
import org.springframework.stereotype.Service;

/**
 * @author : xuemingqi
 * @since : 2025/09/10 14:09
 */
@Service
public class UsersIServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersIService {
}
