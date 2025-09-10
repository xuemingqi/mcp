package com.x.framework.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回结构
 *
 * @author xuemingqi
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResult<T> implements Serializable {

    private int code;

    private String msg;

    private T data;
}
