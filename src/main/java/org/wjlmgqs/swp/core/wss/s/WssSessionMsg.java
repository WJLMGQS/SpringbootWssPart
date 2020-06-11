package org.wjlmgqs.swp.core.wss.s;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.wjlmgqs.swp.core.enums.WssSessionType;
import org.wjlmgqs.swp.core.utils.DataUtils;
import org.wjlmgqs.swp.core.utils.DateUtils;

import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
public class WssSessionMsg {

    /**
     * 会话响应状态
     */
    public static final String SESSION_CODE_SUCC = "0";
    public static final String SESSION_CODE_FAIL = "-1";

    /**
     * 状态码
     */
    private String code = WssSessionMsg.SESSION_CODE_SUCC;

    /**
     * 状态描述
     */
    private String msg;

    /**
     * 会话类型：打开会话 、 业务通信
     * @see WssSessionType
     */
    private String sessionType;

    /**
     * 会话时间
     */
    private long sessionTime;

    /**
     * 会话数据(JSON格式数据)
     */
    private String data;

    /**
     * 会话唯一标识，需要响应式通信的时候设置
     */
    private String uuid;


    public WssSessionMsg buildUUID(String clientId) {//7 + 4 + 6 + 18
        this.uuid = clientId + DateUtils.format(new Date(), "HHmmss") + DataUtils.buildUUID(18);
        return this;
    }


    public WssSessionMsg initSessionTime() {
        this.sessionTime = new Date().getTime();
        return this;
    }


    public static String getSessionCodeSucc() {
        return SESSION_CODE_SUCC;
    }

    public static String getSessionCodeFail() {
        return SESSION_CODE_FAIL;
    }

    public String getCode() {
        return code;
    }

    public WssSessionMsg setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public WssSessionMsg setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getSessionType() {
        return sessionType;
    }

    public WssSessionMsg setSessionType(String sessionType) {
        this.sessionType = sessionType;
        return this;
    }

    public long getSessionTime() {
        return sessionTime;
    }

    public WssSessionMsg setSessionTime(long sessionTime) {
        this.sessionTime = sessionTime;
        return this;
    }

    public String getData() {
        return data;
    }

    public WssSessionMsg setData(String data) {
        this.data = data;
        return this;
    }
}
