package com.x.framework.common.exception.enums;

import java.io.Serializable;

public interface BaseErrorCode extends Serializable {

    /**
     * 状态码
     *
     * @return int
     */
    Integer getCode();

    /**
     * 消息
     *
     * @return String
     */
    String getMsg();

}
