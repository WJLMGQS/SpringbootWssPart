package org.wjlmgqs.swp.core.wss.s;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.wjlmgqs.swp.core.enums.WssClientType;
import org.wjlmgqs.swp.core.enums.WssSessionType;
import org.wjlmgqs.swp.core.utils.RedisUtils;

import javax.websocket.Session;
import java.io.IOException;


public abstract class AbstractWssSocketServer<T> implements ApplicationContextAware {

    public static final Logger LOG = LoggerFactory.getLogger(WssSocketServer.class);

    private IWssSessionService wssSessionService;

    /**
     * 上一次心跳检测时间，小于30秒内的一律强制关闭会话
     */
    private long lastHeartTimer;

    /**
     * wss类型
     */
    private WssClientType wssType;

    /**
     * 会话参数信息
     */
    protected WssSession wssSession;


    public WssClientType getWssType() {
        return wssType;
    }

    public WssSession getWssSession() {
        return wssSession;
    }

    public AbstractWssSocketServer<T> setWssSession(WssSession wssSession) {
        this.wssSession = wssSession;
        return this;
    }

    /**
     * 关闭没有经过open验证的会话
     *
     * @param msg         客户端消息
     * @param session     会话
     * @param sessionType 会话类型
     */
    protected boolean closeUnsafeSession(String msg, Session session, WssSessionType sessionType) {
        if (this.wssSession == null || this.wssSession.getClientId() == null) {
            try {
                session.close();//会话失败
            } catch (IOException e) {
                LOG.info("{} ，关闭远程客户端异常 type -> {} , 接口客户端请求消息msg -> {} ，err -> {} ", this.getWssType().getValue(), sessionType.getValue(), msg, e);
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public void deleSocketSession() {
        if (this.getWssSession() == null || this.getWssSession().getClientId() == null) {
            return;
        }
        try {
            String sessionCacheKey = AbstractWssSessionService.sessionCacheKey(this.getWssSession().getClientId(), this.getWssType());
            RedisUtils.remove(sessionCacheKey);
            if (AbstractWssSessionService.getSocketSessions(this.getWssType()).containsKey(this.getWssSession().getClientId())) {
                AbstractWssSessionService.getSocketSessions(this.getWssType()).remove(this.getWssSession().getClientId());//暂时不做close，没有关闭客户端的需求，现在都是主动关闭
            }
            LOG.info("{} ，客户端关闭 clientId -> {}", this.getWssType().getValue(), this.getWssSession().getClientId());
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("{} ，客户端关闭异常 clientId -> {}", this.getWssType().getValue(), this.getWssSession().getClientId());

        }
    }

    public static ApplicationContext APPLICATION_CONTEXT;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT = applicationContext;
    }


    public IWssSessionService getWssSessionService() {
        if (this.wssSessionService == null) {
            this.wssSessionService = APPLICATION_CONTEXT.getBean(AbstractWssSessionService.WSS_SESSION_SERVICES.get(this.wssType));
        }
        return this.wssSessionService;
    }

    public T setWssType(WssClientType wssType) {
        this.wssType = wssType;
        return (T) this;
    }

    public String getSessionInfo() {
        return this.getWssSession() == null ? "" : (" clientId -> " + this.getWssSession().getClientId());
    }

    public long getLastHeartTimer() {
        return lastHeartTimer;
    }

    public AbstractWssSocketServer<T> setLastHeartTimer(long lastHeartTimer) {
        this.lastHeartTimer = lastHeartTimer;
        return this;
    }
}
