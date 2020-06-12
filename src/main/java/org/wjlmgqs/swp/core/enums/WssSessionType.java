package org.wjlmgqs.swp.core.enums;


/**
 * 客户端与服务端通讯数据包中的通讯类型字段
 */
public enum WssSessionType implements EnumInterface {

    /**
     * 部分服务会在nginx端限制长链接的会话时间，例如：当一分钟没有访问时，自动断开；
     * 所以需要一个心跳检测来保证不会被打断会话
     * 但是心跳检测太频繁也会给服务端造成压力，目前限制访问频率不能小于45秒
     */
    HEART("0000", "心跳检测"),

    /**
     * 客户端与服务端建立长链接的第一步：加入会话
     */
    OPEN("0001", "新增会话"),

    /**
     * 服务端主动向客户端发送请求
     */
    SERVER("0002", "服务端请求客户端"),

    /**
     * 客户端主动向服务端发送请求
     */
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
