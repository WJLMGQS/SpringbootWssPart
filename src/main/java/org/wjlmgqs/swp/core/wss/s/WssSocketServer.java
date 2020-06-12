package org.wjlmgqs.swp.core.wss.s;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.wjlmgqs.swp.core.constant.SwpConstants;
import org.wjlmgqs.swp.core.enums.WssClientType;
import org.wjlmgqs.swp.core.enums.WssSessionType;
import org.wjlmgqs.swp.core.utils.EnumUtils;
import org.wjlmgqs.swp.core.utils.RedisUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Date;

/**
 * websockt服务中心：叫号、医保
 * 汇总登记websocket客户端
 *
 * @author wjlmgqs@sina.com
 * @date 2018/10/28 14:04
 */
@ServerEndpoint(value = "/wss/{action}")
@Component
@Slf4j
public class WssSocketServer extends AbstractWssSocketServer {

    @OnOpen
    public void onOpen(Session session, @PathParam("action") String action) {
        if (AbstractWssSessionService.WSS_SESSION_SERVICES.containsKey(action)) {
            super.setWssType(WssClientType.get(action));
        } else {//直接关闭无效的请求
            super.closeUnsafeSession("无效的wss服务类型参数", session, WssSessionType.OPEN);
        }
        log.info("{}服务，发现新客户连接 action -> {} ", this.getWssType().getValue(), action);
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
        //解析客户端数据包
        WssSessionMsg sessionMsg = JSON.parseObject(msg, WssSessionMsg.class);
        long currTime = new Date().getTime();//当前时间
        WssSessionType sessionType = EnumUtils.getEnumByCode(WssSessionType.class, sessionMsg.getSessionType());//当前数据包的通讯会话类型
        if (WssSessionType.HEART == sessionType) {//心跳检测：心跳检测频次限制
            long heart = currTime - this.getLastHeartTimer() - SwpConstants.WSS_TIMER_CONNECT_HEART * 1000;//超过指定时间多少秒
            boolean flag = this.getLastHeartTimer() > 0 && heart < 0;//2次请求间隔小于指定时间
            log.info("【客户端心跳检测】" + (flag ? "【强制屏蔽】" : "") + " SessionId -> {} , 上一次访问时间 lastTimer -> {} , 当前时间 currTimer -> {} , 间隔 -> {} , 会话信息[{}]  ",
                    session.getId(), this.getLastHeartTimer(), currTime, (currTime - this.getLastHeartTimer()), this.getSessionInfo());
            if (flag) {//强制关闭30秒内心跳检测的客户端
                getWssSessionService().sendClientSessionMsg(session, sessionMsg.setData("客户端心跳检测请求频次过高(" + heart + ")"));
                this.setWssSession(null);//设置会话无效
                this.closeUnsafeSession(msg, session, sessionType);//强制关闭
                this.deleSocketSession();
            }
            this.setLastHeartTimer(currTime);//更新心跳检测时间点
            return;
        }
        log.info("接收消息[{}] -> {} , 会话信息[{}], msg -> {} ", currTime, sessionType.getValue(), this.getWssType().getValue(), this.getSessionInfo(), msg);
        if (WssSessionType.OPEN == sessionType) {//要求加入会话：新加入的通信会话，都必须发起一次打招呼
            WssSession wssSession = super.getWssSessionService().joinSession(session, sessionMsg);
            super.setWssSession(wssSession);
            this.closeUnsafeSession(msg, session, sessionType);
        } else if (WssSessionType.SERVER == sessionType) {//业务通信-云管家请求客户端
            String msgCacheKey = getWssSessionService().msgCacheKey(sessionMsg.getUuid());
            RedisUtils.set(msgCacheKey, sessionMsg, AbstractWssSessionService.CACHE_TIME_DATA_EXPIRE_10000);
        } else if (WssSessionType.CLIENT == sessionType) {//业务通信-客户端请求云管家
            if (!this.closeUnsafeSession(msg, session, sessionType)) {//关闭非法的会话：只有经过open操作设置了clientId的才有效
                getWssSessionService().queryCallback(this.getWssSession(), session, sessionMsg);
            }
        } else {//其他不处理

        }
    }

    @OnClose
    public void onClose() {
        deleSocketSession();
    }

    @OnError
    public void onError(Throwable error) {
        log.error("{} ，客户端关闭 {} , err -> {} ", this.getWssType().getValue(), this.getSessionInfo(), error.getMessage());
        error.printStackTrace();
    }


}