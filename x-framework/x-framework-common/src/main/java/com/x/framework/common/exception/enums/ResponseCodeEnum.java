package com.x.framework.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xuemingqi
 * @since : 2025/09/10 14:22
 */
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseErrorCode {

    SUCCESS(200, "success!"),
    BAD_REQUEST(400, "参数错误！"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    FILE_NOT_FOUND(404, "不存在"),
    METHOD_NOT_ALLOWED(405, "请求方式错误！"),
    TOO_MANY_REQUESTS(429, "too many requests"),
    INTERNAL_SERVER_ERROR(500, "发生未知错误，请联系管理员！");

    private final Integer code;
    private final String msg;
}
