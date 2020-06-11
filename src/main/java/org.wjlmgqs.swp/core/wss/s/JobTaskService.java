package org.wjlmgqs.swp.core.wss.s;

public interface JobTaskService {

    /**
     * 定时刷新缓存中的会话客户端记录
     */
    void exec(Runnable runnable);

}
