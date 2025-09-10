package com.x.mcp.server.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.framework.db.entity.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author : xuemingqi
 * @since : 2025/09/10 14:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("users")
public class Users extends BaseEntity {

    @TableId("id")
    private Long id;

    @TableField("token")
    private String token;

    @TableField("remark")
    private String remark;
}
