package org.wjlmgqs.swp.core.enums;


public enum WssSessionType implements EnumInterface {

    HEART("0000", "心跳检测"),
    OPEN("0001", "新增会话"),
    SERVER("0002", "服务端请求客户端"),
    CLIENT("0003", "客户端请求服务端"),
    ;

    private String code;
    private String value;

    WssSessionType(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getName() {
        return this.name();
    }

    public String getCode() {
        return this.code;
    }

    public String getValue() {
        return this.value;
    }

}
