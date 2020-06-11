package org.wjlmgqs.swp.core.exps;

public class CustomizedException extends RuntimeException {

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误信息
     */
    private String msg;

    public CustomizedException(String msg) {
        this.msg = msg;
    }

    public CustomizedException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public String getCode() {
        return code;
    }

    public CustomizedException setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public CustomizedException setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}