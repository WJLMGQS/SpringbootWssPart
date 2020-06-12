package org.wjlmgqs.swp.core.wss.s;

import javax.websocket.Session;

public interface IWssSessionService {

    /**
     * 加入会话
     */
    WssSession joinSession(Session session, WssSessionMsg sessionMsg);

    /**
     * 向客户端输出数据
     */
    void queryCallback(WssSession wssSession, Session session, WssSessionMsg data);

    /**
     * 向指定会话发送消息
     */
    void sendClientSessionMsg(Session session, WssSessionMsg msg);

    /**
     * 根据uuid获取redis中的通讯响应消息
     */
    String msgCacheKey(String uuid);

    /**
     * lock缓存失败重试最大次数及间隔时间
     */
    int CACHE_LOCK_RETRY_3 = 3;//最大尝试次数

    long CACHE_LOCK_RETRY_TIME_200 = 200;//重试间隔时间

    int CACHE_TASK_MAX_SIZE_300 = 300;//缓存队列的呼叫任务最大上限

    int CACHE_TASK_CONSUME_MAX_SIZE_10 = 10;//同一诊所 呼叫任务消费数量单位

    int SLEEP_TIMER_250 = 250;//0.25秒

    int CACHE_TIME_DATA_EXPIRE_10000 = 10;//缓存中数据失效时间

    int CACHE_TIME_DATA_EXPIRE_TIMER_10000 = 10000;//缓存中数据失效时间

    int CACHE_TIME_DATA_SESSION_EXPIRE_45_1000 = 45;//保存缓存中诊所通讯状态的有效时间

    int HTTP_SLEEP_TIME_MAX_10000 = 10000;//缓存中数据失效时间

    int CACHE_TIME_LOCK_EXPIRE_1000 = 1;//缓存中lock失效时间
}
