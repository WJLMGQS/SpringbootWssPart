package org.wjlmgqs.swp.core.wss.s;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.wjlmgqs.swp.core.enums.WssClientType;
import org.wjlmgqs.swp.core.utils.EnumUtils;
import org.wjlmgqs.swp.core.utils.RedisUtils;

import javax.websocket.Session;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WssTaskServiceImpl implements WssTaskService {

    @Autowired
    private JobTaskService jobTaskService;

    protected static final Logger LOG = LoggerFactory.getLogger(WssTaskServiceImpl.class);

    /**
     * 会话同步时间
     */
    protected static final int TIMER_SESSION_TIME_REGISTER_15_1000 = 1000 * 15;//15秒刷新一次缓存会话信息

    /**
     * 发送和读取消息的定时任务时间间隔
     */
    protected static final int TIMER_TASK_TIME_1500 = 1500;


    /**
     * 会话客户端 - 定时刷新缓存中的记录
     */
    @Async
    @Scheduled(initialDelay = TIMER_TASK_TIME_1500, fixedRate = TIMER_SESSION_TIME_REGISTER_15_1000)
    public void timerRegisterSession() {
        AbstractWssSessionService.WSS_SESSIONS.forEach((wssClientType, sessions) -> {
            sysSessions(sessions, WssClientType.get(wssClientType));
        });
    }

    /**
     * 定时发送任务
     */
    @Scheduled(initialDelay = TIMER_TASK_TIME_1500, fixedRate = TIMER_TASK_TIME_1500)
    public void timerTasks() {
        jobTaskService.exec(() -> {//发送任务
            AbstractWssSessionService.WSS_TASKS.forEach((wssClientType, tasks) -> syncTaskProduct(tasks, WssClientType.get(wssClientType)));
        });
        jobTaskService.exec(() -> {//读取任务结果
            AbstractWssSessionService.WSS_SESSIONS.forEach((wssClientType, tasks) -> syncConsumeTask(tasks, WssClientType.get(wssClientType)));
        });
    }

    /**
     * 同步wss会话到redis
     */
    private void sysSessions(ConcurrentHashMap<String, Session> wssSession, WssClientType wssClientType) {
        try {
            Iterator<Map.Entry<String, Session>> iterator = wssSession.entrySet().iterator();
            String key;
            List<String> clientIds = new ArrayList<>();
            while (iterator.hasNext()) {
                key = iterator.next().getKey();
                RedisUtils.set(AbstractWssSessionService.sessionCacheKey(key, wssClientType)//当前诊所任务缓存队列key
                        , key, AbstractWssSessionService.CACHE_TIME_DATA_SESSION_EXPIRE_45_1000);
                clientIds.add(key);
            }
            if (clientIds.size() > 0) {
                LOG.info("ThreadId -> {} , 同步[{}]客户会话到 redis wss keySize -> {} , keys -> {} ", Thread.currentThread().getId(), wssClientType.getValue(), clientIds.size(), StringUtils.join(clientIds, ","));
            }
        } catch (Exception e) {
            LOG.error("[{}]服务-定时任务-客户端注册 出现异常 -> {} ", wssClientType.getValue(), e);
            e.printStackTrace();
        }
    }


    /**
     * 消费服务端发送给客户端的消息，构建消息通知任务
     */
    private void syncTaskProduct(ConcurrentHashMap<String, WssSessionTaskCahceList> tasks, WssClientType wssClientType) {
        try {
            Iterator<Map.Entry<String, WssSessionTaskCahceList>> iterator = tasks.entrySet().iterator();
            String clientId;
            Map.Entry<String, WssSessionTaskCahceList> taskCaches;
            while (iterator.hasNext()) {
                taskCaches = iterator.next();
                clientId = taskCaches.getKey();
                WssSessionTaskCahceList callTasks = taskCaches.getValue();
                if (callTasks.getTasks().size() == 0) {
                    continue;
                }
                String taskCacheKey = AbstractWssSessionService.taskCacheKey(clientId, wssClientType);//当前诊所任务缓存队列key
                String taskLockKey = AbstractWssSessionService.taskLockCacheKey(clientId, wssClientType);//lock 的keylong timer =
                long timer = new Date().getTime();
                try {
                    long l = AbstractWssSessionService.addCacheLock(clientId, timer, taskLockKey, wssClientType, AbstractWssSessionService.CACHE_LOCK_RETRY_3);//添加当前诊所的缓存操作锁
                    if (l == 0) {//锁定失败，等待下次
                        continue;
                    }
                    WssSessionTaskCahceList taskCahceList = AbstractWssSessionService.readCacheTaskList(taskCacheKey);//读区缓存中已有的任务信息
                    synchronized (callTasks.getTasks()) {//同步处理，是因为有前端界面添加任务的关系
                        List<WssSessionTaskCahce> tempTask = callTasks.getTasks();
                        while (taskCahceList.getTasks().size() < AbstractWssSessionService.CACHE_TASK_MAX_SIZE_300 && tempTask.size() > 0) {//将当前容器中的任务添加到缓存中，等待消费
                            taskCahceList.getTasks().add(tempTask.remove(0));
                        }
                    }
                    String taskCacheStr = JSON.toJSONString(taskCahceList);//序列化任务
                    RedisUtils.set(taskCacheKey, taskCacheStr, AbstractWssSessionService.CACHE_TIME_DATA_EXPIRE_10000);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.error("{}服务-同步任务 添加任务异常 clientId -> {} , err： -> {} ", wssClientType.getValue(), clientId, e.getMessage());
                } finally {
                    AbstractWssSessionService.deleCacheLock(taskLockKey, timer);//移除标示
                }
            }
        } catch (Exception e) {
            LOG.error("{}服务-定时任务-构建消息任务 出现异常 -> {} ", wssClientType.getValue(), e);
            e.printStackTrace();
        }
    }


    /**
     * 解析客户端发送给服务端的消息，加入redis实现读取
     */
    private void syncConsumeTask(ConcurrentHashMap<String, Session> wssSession, WssClientType wssClientType) {
        try {
            Iterator<Map.Entry<String, Session>> iterator = wssSession.entrySet().iterator();
            while (iterator.hasNext()) {
                String clientId = iterator.next().getKey();
                String taskCacheKey = AbstractWssSessionService.taskCacheKey(clientId, wssClientType);//当前诊所任务缓存队列key
                String taskLockKey = AbstractWssSessionService.taskLockCacheKey(clientId, wssClientType);//lock 的key
                List<WssSessionTaskCahce> taskList = new ArrayList<>();//待执行的任务统计
                long currTimer = new Date().getTime();
                WssSessionTaskCahce taskCache;
                try {
                    long l = AbstractWssSessionService.addCacheLock(clientId, currTimer, taskLockKey, wssClientType, AbstractWssSessionService.CACHE_LOCK_RETRY_3);
                    if (l == 0) {//锁定失败，等待下次
                        LOG.info("{}服务-消费任务 任务锁等待过长 clientId -> {} , taskLockKey -> {} ", wssClientType.getValue(), clientId, taskLockKey);
                        continue;
                    }
                    WssSessionTaskCahceList taskCahceList = AbstractWssSessionService.readCacheTaskList(taskCacheKey);
                    if (taskCahceList.getTasks().size() == 0) {
                        continue;//跳过
                    }
                    //移除超时的任务
                    while (taskList.size() < AbstractWssSessionService.CACHE_TASK_CONSUME_MAX_SIZE_10 && taskCahceList.getTasks().size() > 0) {//代执行数量小于上限 && 任务列表还有任务
                        taskCache = taskCahceList.getTasks().remove(0);
                        if (currTimer - taskCache.getTime() > AbstractWssSessionService.CACHE_TIME_DATA_EXPIRE_TIMER_10000) {
                            LOG.info("{}服务-消费任务 移除超时任务 clientId -> {} , task -> {} ", wssClientType.getValue(), clientId, JSON.toJSONString(taskCache));
                            continue;
                        }
                        taskList.add(taskCache);//消费第一个任务
                    }
                    taskCahceList.setClientId(clientId);
                    String taskCacheStr = JSON.toJSONString(taskCahceList);//序列化任务
                    RedisUtils.set(taskCacheKey, taskCacheStr, AbstractWssSessionService.CACHE_TIME_DATA_EXPIRE_10000);
                } catch (Exception e) {
                    LOG.error("{}服务-消费任务 出现异常：clientId ->{} , err -> ", wssClientType.getValue(), clientId, e.getMessage());
                    e.printStackTrace();
                } finally {
                    AbstractWssSessionService.deleCacheLock(taskLockKey, currTimer);//移除标示
                }
                taskList.forEach(task -> {
                    AbstractWssSessionService.sendSocketMsg(clientId, task.getMsg(), wssSession, wssClientType);//发送信息到客户端
                });
            }
        } catch (Exception e) {
            LOG.error("{}服务-定时任务-消费任务 出现异常 -> {} ", wssClientType.getValue(), e);
            e.printStackTrace();
        }
    }
}
