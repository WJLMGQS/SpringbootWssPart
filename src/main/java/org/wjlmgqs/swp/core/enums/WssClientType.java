package org.wjlmgqs.swp.core.enums;

import org.springframework.stereotype.Service;
import org.wjlmgqs.swp.core.wss.s.AbstractWssSessionService;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端类型定义
 */
public class WssClientType {

    /**
     * 所有已实现AbstractWssSessionService接口的客户端服务自动注册服务类型到该Map
     * @see AbstractWssSessionService
     */
    private static Map<String, WssClientType> ENUM_MAPS = new HashMap<>();

    /**
     * 客户端类型 - 代码
     */
    private String code;

    /**
     * 客户端类型 - 描述
     */
    private String value;

    WssClientType(String code, String value) {
        this.code = code;
        this.value = value;
    }

    /**
     * 添加枚举类型
     */
    public static WssClientType build(String code, String value) {
        WssClientType wssClientType = new WssClientType(code, value);
        ENUM_MAPS.put(code, wssClientType);
        return wssClientType;
    }

    /**
     * 添加枚举类型
     */
    public static WssClientType get(String code) {
        return ENUM_MAPS.get(code);
    }

    public String getCode() {
        return code;
    }

    public WssClientType setCode(String code) {
        this.code = code;
        return this;
    }

    public String getValue() {
        return value;
    }

    public WssClientType setValue(String value) {
        this.value = value;
        return this;
    }
}
