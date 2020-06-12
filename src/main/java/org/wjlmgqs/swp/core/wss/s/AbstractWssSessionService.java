package org.wjlmgqs.swp.core.wss.s;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.wjlmgqs.swp.core.constant.SwpRedisKeys;
import org.wjlmgqs.swp.core.enums.WssClientType;
import org.wjlmgqs.swp.core.enums.WssSessionType;
import org.wjlmgqs.swp.core.exps.SwpCustomizedException;
import org.wjlmgqs.swp.core.utils.RedisUtils;
import org.wjlmgqs.swp.core.utils.StrUtils;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Wss服务实现基类，所有对接的客户端实现都必须继承该类
 */
@Slf4j
public abstract class AbstractWssSessionService implements IWssSessionService {

    @Autowired
    protected DozerBeanMapper dozerBeanMapper;

    /**
     * 定义所有实现了的wss服务
     */
    public static Map<String, Class<? extends IWssSessionService>> WSS_SESSION_SERVICES = new ConcurrentHashMap<>();

    /**
     * 会话的通讯任务集合：<wssSessionType , <taskId , List<task> > >
     */
    public static Map<String, ConcurrentHashMap<String, WssSessionTaskCahceList>> WSS_TASKS = new HashMap<>();

    /**
     * 会话的通讯会话集合：<wssSessionType , <taskId , Session > >
     */
    public static Map<String, ConcurrentHashMap<String, Session>> WSS_SESSIONS = new HashMap<>();


    /**
     * 服务注册到服务池里
     */
    public AbstractWssSessionService() {
        //客户端实现类注册到服务池
        WSS_SESSION_SERVICES.put(this.getWssClientType().getCode(), this.getClass());
        //初始化客户端会话和任务池
        WSS_TASKS.put(this.getWssClientType().getCode(), new ConcurrentHashMap<>());
        WSS_SESSIONS.put(this.getWssClientType().getCode(), new ConcurrentHashMap<>());
        log.info("...wss服务 注册客户端服务 添加wssType -> {} ");
    }

    /**
     * wss类型:所有实现类都需要声明自己的类型
     */
    public abstract WssClientType getWssClientType();

    /**
     * 客服端服务获取所有的通讯任务队列
     */
    public ConcurrentHashMap<String, WssSessionTaskCahceList> getSocketTasks() {
        return WSS_TASKS.get(this.getWssClientType().getCode());
    }


    /**
     * 客服端服务获取所有的通讯会话
     */
    public ConcurrentHashMap<String, Session> getSocketSessions() {
        return WSS_SESSIONS.get(this.getWssClientType().getCode());
    }


    /**
     * 客服端服务获取所有的通讯会话
     */
    public static ConcurrentHashMap<String, Session> getSocketSessions(WssClientType wssType) {
        return WSS_SESSIONS.get(wssType.getCode());
    }


    /**
     * redis key 操作
     */
    public String msgCacheKey(String uuid) {
        return SwpRedisKeys.getBuzKey(SwpRedisKeys.FUNC_WSS_CLIENT_MSG + this.getWssClientType().getCode() + ":", uuid);
    }

    public static String sessionCacheKey(String clientId, WssClientType wssType) {
        return SwpRedisKeys.getBuzKey(SwpRedisKeys.FUNC_WSS_CLIENT_SESSION + wssType.getCode() + ":", clientId);
    }

    public static String taskCacheKey(String clientId, WssClientType wssType) {
        return SwpRedisKeys.getBuzKey(SwpRedisKeys.FUNC_WSS_TASK_LIST + wssType.getCode() + ":", clientId);
    }

    public static String taskLockCacheKey(String clientId, WssClientType wssType) {
        return SwpRedisKeys.getBuzKey(SwpRedisKeys.FUNC_WSS_TASK_LOCK + wssType.getCode() + ":", clientId);
    }


    /**
     * 客户端请求加入会话，服务端返回加入成功消息
     */
    public WssSession joinSession(Session session, WssSessionMsg sessionMsg) {
        WssSessionMsgOpenSession openSession = JSON.parseObject(sessionMsg.getData(), WssSessionMsgOpenSession.class);
/**     TODO：校验openSession的合法性，可以根据自己需要扩展该对象，例如加入签名校验参数信息等
 if (false) {
 sendClientSessionMsg(session , sessionMsg.setData(errMsg));
 return null;
 }
 */
        String clientId = openSession.getClientId();//可以自己定义clientId取值
        try {
            addSocketSession(session, clientId);
            JSONObject results = new JSONObject();//结果数据对象
            //加入会话成功的时候，返回当前机构需要初始化的员工信息
            results.put("clientId", clientId);
            results.put("msg", "success");
            String msg = JSON.toJSONString(results);
            sendClientSessionMsg(session, sessionMsg.setData(msg));
            log.info("{}服务 clientId -> {} ，成功加入会话 , msg -> {} ", this.getWssClientType().getCode(), clientId, msg);
        } catch (Exception e) {
            log.error("{}服务 clientId -> {} ，加入会话失败 , msg -> {} ", this.getWssClientType().getCode(), clientId, JSON.toJSONString(sessionMsg));
            e.printStackTrace();
            return null;
        }
        return new WssSession().setClientId(clientId);
    }


    /**
     * 添加客户端会话
     * @param session
     * @param clientId
     */
    public void addSocketSession(Session session, String clientId) {
        //会话客户端写入缓存(不存在并发情况，主要是为了共享)
        String sessionCacheKey = sessionCacheKey(clientId, this.getWssClientType());
        RedisUtils.set(sessionCacheKey, clientId, CACHE_TIME_DATA_SESSION_EXPIRE_45_1000);
        this.getSocketSessions().put(clientId, session);
        log.info(" {} 服务，SessionId -> {} , 诊所 -> {} ,加入WSS服务通信", this.getWssClientType().getValue(), session.getId(), clientId);
    }


    /**
     * 发送业务消息，需要暂存会话，等客户端响应后唤醒
     */
    public <T extends WssSessionMsg> T sendBusiMsg(String clientId, WssSessionMsgData data, long maxCostTime, Class<T> tClass) {

        String clinciSessionKey = sessionCacheKey(clientId + "", this.getWssClientType());

        //诊所渠道
        String clinicKey = RedisUtils.get(clinciSessionKey, String.class);
        if (StringUtils.isEmpty(clinicKey)) {
            throw new SwpCustomizedException("客户端[" + this.getWssClientType().getValue() + "](" + clientId + ")未链接");
        }
        long currTimer = new Date().getTime();
        //构建会话消息内容
        String message = JSON.toJSONString(new WssSessionMsg()
                .setSessionType(WssSessionType.SERVER.getCode())
                .setData(StrUtils.toJSONString(data))
                .setSessionTime(currTimer)
                .buildUUID(clinicKey))
                ;//uuid标示一次请求会话
        //将消息保存到缓存中，等待消费
        saveTask(clinicKey,
                new WssSessionTaskCahce()
                        .setMsg(message)
                        .setTime(currTimer));

        String msgCacheKey = msgCacheKey(clinicKey);
        return readClientMsg(clinicKey, msgCacheKey, currTimer, this.getWssClientType(), maxCostTime, tClass); //定时从消息池中读取对应uuid的消息
    }

    public static <T extends WssSessionMsg> T readClientMsg(String clientId, String msgCacheKey, long currTimer, WssClientType wssType, long maxCostTime, Class<T> tClass) {
        //休眠一段时间，从会话队列中读取客户端响应的数据（等待的时候，需要保证足够客户端响应）
        T result;
        try {
            String msgTmp;
            while (true) {
                Thread.sleep(AbstractWssSessionService.SLEEP_TIMER_250);
                long costTime = new Date().getTime() - currTimer;
                msgTmp = RedisUtils.get(msgCacheKey, String.class);
                if (!StringUtils.isEmpty(msgTmp)) {//已经成功返回消息
                    result = JSON.parseObject(msgTmp, tClass);
                    log.debug("{}服务 读取客户端响应成功 clientId -> {} , msgCacheKey -> {} , costTime -> {} , result -> {} ", wssType.getValue(), clientId, msgCacheKey, costTime, JSON.toJSONString(result));
                    break;
                }
                if (costTime >= maxCostTime) {//超时判断
                    log.error("{}服务 读取客户端响应超时 clientId -> {} , msgCacheKey -> {} , maxCostTime -> {} , costTime -> {} ， request data -> {} ", wssType.getValue(), clientId, msgCacheKey, maxCostTime, costTime);
                    throw new SwpCustomizedException(wssType.getValue() + "客户端请求超时");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new SwpCustomizedException(wssType + "服务端读取客户端响应异常，请稍后再试！");
        }
        return result;
    }

    public static void sendSocketMsg(String clientId, String msg, ConcurrentHashMap<String, Session> socketSessions, WssClientType wssType) {
        if (socketSessions.get(clientId) != null) {
            socketSessions.get(clientId).getAsyncRemote().sendText(msg);
            log.debug("{}服务 发送信息给客户端 clientId -> {} , msg -> {}  ", wssType.getValue(), clientId, msg);
        }
    }


    /**
     * 添加呼叫任务 , 等待缓存同步
     *
     * @param taskCahce 呼叫任务
     */
    public void saveTask(String clientId, WssSessionTaskCahce taskCahce) {
        ConcurrentHashMap<String, WssSessionTaskCahceList> socketTasks = this.getSocketTasks();
        synchronized (socketTasks) {
            if (!socketTasks.containsKey(clientId)) {
                socketTasks.put(clientId, new WssSessionTaskCahceList().setClientId(clientId));
            }
        }

        synchronized (socketTasks.get(clientId)) {//一个诊所会话的信息限制在300数量
            List<WssSessionTaskCahce> tasks = socketTasks.get(clientId).getTasks();
            if (tasks.size() >= AbstractWssSessionService.CACHE_TASK_MAX_SIZE_300) {
                throw new SwpCustomizedException(this.getWssClientType().getValue() + "任务繁忙，请稍后再试！");
            }
            tasks.add(taskCahce);//加入到逻辑队列中，等待定时任务刷新到缓存
        }
    }

    public static WssSessionTaskCahceList readCacheTaskList(String taskCacheKey) {
        WssSessionTaskCahceList taskList;//缓存中的任务队列
        String taskCacheStr = RedisUtils.get(taskCacheKey, String.class);
        if (StringUtils.isEmpty(taskCacheStr)) {
            taskList = new WssSessionTaskCahceList();
        } else {
            taskList = JSON.parseObject(taskCacheStr, WssSessionTaskCahceList.class);
        }
        return taskList;
    }

    /**
     * 移除诊所任务操作标志状态
     *
     * @param lockKey 锁key值
     * @return 是否标志成功
     */
    public static void deleCacheLock(String lockKey, long timer) {
        Long t = RedisUtils.get(lockKey, Long.class);
        if (t != null && t.longValue() == timer) {
            RedisUtils.remove(lockKey);
        }
    }


    /**
     * 尝试占用修改缓存操作锁
     *
     * @param clientId 诊所ID
     * @return 占用成功时返回时间戳 ， 失败的时候返回0
     */
    public static long addCacheLock(String clientId, long timer, String lockKey, WssClientType wssType, int retryCount) {
        int count = 0;//统计占用锁次数
        boolean flag = false;
        while (!flag) {//尝试锁定
            flag = RedisUtils.setnx(lockKey, timer, AbstractWssSessionService.CACHE_TIME_LOCK_EXPIRE_1000);
            if (count++ >= retryCount) {//超过最大重试次数
                break;
            }
            try {
                Thread.sleep(AbstractWssSessionService.CACHE_LOCK_RETRY_TIME_200);//0.2秒
            } catch (Exception e) {
                log.error("{}服务>>>>>>>>>>添加任务锁失败，等待下次 诊所：lockKey -> {}  , clientId -> {} , e ->  {} ", wssType.getValue(), lockKey, clientId, e.getMessage());
            }
        }
        return flag ? timer : 0;
    }

    /**
     * 发送业务消息，需要暂存会话，等客户端响应后唤醒
     */
    public void sendClientSessionMsg(Session session, WssSessionMsg msg) {
        String str = JSON.toJSONString(msg.initSessionTime());
        synchronized (session) {
            try {
                session.getBasicRemote().sendText(str);
                log.debug("{}服务 ，发送会话信息 msg -> {} ", getWssClientType().getCode(), str);
            } catch (IOException e) {
                log.error("wss发送给客户端消息异常 session -> {} , msg -> {} ", session.getId(), str);
                e.printStackTrace();
            }
        }
    }


    /**
     * 处理客户端请求，默认实现
     */
    @Override
    public void queryCallback(WssSession wssSession, Session session, WssSessionMsg data) {
        log.debug("{}服务 ，默认处理客户端请求 ...... 啥也不干", getWssClientType().getCode());
        data.setCode(WssSessionMsg.SESSION_CODE_SUCC);
        this.sendClientSessionMsg(session, data);
    }

}
