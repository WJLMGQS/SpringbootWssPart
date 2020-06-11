package org.wjlmgqs.swp.core.wss.s;

public interface WssTaskService {

    /**
     * 定时刷新缓存中的会话客户端记录
     */
    void timerRegisterSession();


    /**
     * 定时添加诊所呼叫任务-叫号
     */
    void timerTasks();

}
