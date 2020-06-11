package org.wjlmgqs.swp.core.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端类型定义
 */
public class WssClientType {

    private static Map<String, WssClientType> ENUM_MAPS = new HashMap<>();

    private String code;

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
