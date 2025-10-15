package com.x.mcp.client.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : xuemingqi
 * @since : 2025/10/15 13:50
 */
@Getter
@AllArgsConstructor
public enum AuthType {

    BASIC("basic", "Basic "),

    BEARER("bearer", "Bearer "),

    CUSTOM("custom", "");


    @JsonValue
    private final String value;

    private final String prefix;
}
