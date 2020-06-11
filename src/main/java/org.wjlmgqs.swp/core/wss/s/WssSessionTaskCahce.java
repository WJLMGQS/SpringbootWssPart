package org.wjlmgqs.swp.core.wss.s;

/**
 * 节点间缓存的任务队列对象
 */
public class WssSessionTaskCahce {

    /**
     * 发送给诊所的序列化消息
     */
    private String msg;

    /**
     * 任务加入时间
     */
    private long time;

    public String getMsg() {
        return msg;
    }

    public WssSessionTaskCahce setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public long getTime() {
        return time;
    }

    public WssSessionTaskCahce setTime(long time) {
        this.time = time;
        return this;
    }
}
