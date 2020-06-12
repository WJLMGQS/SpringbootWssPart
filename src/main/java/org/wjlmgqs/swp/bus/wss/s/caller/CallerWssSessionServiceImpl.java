package org.wjlmgqs.swp.bus.wss.s.caller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wjlmgqs.swp.core.enums.WssClientType;
import org.wjlmgqs.swp.core.wss.s.AbstractWssSessionService;
import org.wjlmgqs.swp.core.wss.s.WssSessionMsg;

/**
 * Wss实现 - 叫号服务
 */
@Slf4j
@Service
public class CallerWssSessionServiceImpl extends AbstractWssSessionService {

    /**
     * 声明叫号类型
     */
    public static WssClientType WSS_CLIENT_TYPE = WssClientType.build("caller", "叫号");

    @Override
    public WssClientType getWssClientType() {
        return WSS_CLIENT_TYPE;
    }

    /**
     * 发送业务消息，并暂存会话，等客户端响应后唤醒
     */
    public <T extends WssSessionMsg> T sendBusiMsg(WssCallerCallParam callerCallParam) {
        return (T) super.sendBusiMsg(callerCallParam.getClinicId() + "",//组装成已经连接客户端的标识
                dozerBeanMapper.map(callerCallParam, CallSessionCallMsgData.class),
                WssSessionMsg.class); //定时从消息池中读取对应uuid的消息
    }


}
