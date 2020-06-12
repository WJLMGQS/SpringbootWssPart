package org.wjlmgqs.swp.core.exps;


public class SwpCustomizedException extends RuntimeException {

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误信息
     */
    private String msg;

    public SwpCustomizedException(String msg) {
        this.code = "SWP_00001";
        this.msg = msg;
    }

    public SwpCustomizedException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public String getCode() {
        return code;
    }

    public SwpCustomizedException setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public SwpCustomizedException setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}