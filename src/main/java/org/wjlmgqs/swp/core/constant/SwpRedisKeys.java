package org.wjlmgqs.swp.core.constant;

public class SwpRedisKeys {

    /**
     * 模块为度
     */
    public static final String MODULE = "swp:";

    /**
     * 根据功能类型和业务ID获取
     */
    public static String getBuzKey(String func, String buzId) {
        return MODULE + func + buzId;
    }


    /**
     * wss 任务队列
     */
    public static final String FUNC_WSS_TASK_LIST = "WSS_TASK_LIST:";

    /**
     * wss 客户端 redis共享
     */
    public static final String FUNC_WSS_CLIENT_SESSION = "WSS_CLIENT_SESSION:";

    /**
     * wss 客户端发来的消息 redis共享
     */
    public static final String FUNC_WSS_CLIENT_MSG = "WSS_CLIENT_MSG:";

    /**
     * wss 客户端发来的消息 redis共享
     */
    public static final String FUNC_WSS_TASK_LOCK = "WSS_TASK_LOCK:";



}
