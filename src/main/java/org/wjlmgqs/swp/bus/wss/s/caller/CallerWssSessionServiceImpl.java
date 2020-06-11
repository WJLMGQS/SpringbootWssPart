package org.wjlmgqs.swp.bus.wss.s.caller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wjlmgqs.swp.core.enums.WssClientType;
import org.wjlmgqs.swp.core.wss.s.AbstractWssSessionService;
import org.wjlmgqs.swp.core.wss.s.WssSessionMsgData;

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
     * 发送业务消息，需要暂存会话，等客户端响应后唤醒
     */
    public <T extends WssSessionMsgData> T sendBusiMsg(WssCallerCallParam callerCallParam) {

        String clientId = callerCallParam.getClinicId() + "";//组装成已经连接客户端的标识

        WssSessionMsgData wssSessionMsgData = super.sendBusiMsg(clientId,
                dozerBeanMapper.map(callerCallParam , CallSessionCallMsgData.class),
                AbstractWssSessionService.HTTP_SLEEP_TIME_MAX_10000, WssSessionMsgData.class);

        return (T) wssSessionMsgData; //定时从消息池中读取对应uuid的消息
    }


}
