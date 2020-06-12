package org.wjlmgqs.swp.bus.wss.s.his;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wjlmgqs.swp.core.enums.WssClientType;
import org.wjlmgqs.swp.core.wss.s.AbstractWssSessionService;
import org.wjlmgqs.swp.core.wss.s.WssSession;
import org.wjlmgqs.swp.core.wss.s.WssSessionMsg;

import javax.websocket.Session;
import java.util.Date;

/**
 * Wss实现 - 医保服务
 */
@Slf4j
@Service
public class HisWssSessionServiceImpl extends AbstractWssSessionService {

    @Override
    public WssClientType getWssClientType() {
        return WssClientType.build("his", "医保");
    }

    /**
     * 处理客户端请求服务端
     */
    @Override
    public void queryCallback(WssSession wssSession, Session session, WssSessionMsg sessionMsg) {
        //入参请求
        HisClientSocketParams params = JSON.parseObject(sessionMsg.getData(), HisClientSocketParams.class);
        //构建响应结果
        HisClientSocketResult socketResult = new HisClientSocketResult();

        try {
            /**
             * TODO：根据params查询数据，组装结果对象
             */
            //.....
            socketResult.setResult("哈哈");
        } catch (Exception e) {
            sessionMsg.setCode(WssSessionMsg.SESSION_CODE_FAIL).setMsg(e.getMessage());
            e.printStackTrace();
        } finally {
            super.sendClientSessionMsg(session, socketResult , sessionMsg);
            long currTime = new Date().getTime();
      /*      log.info("医保服务 响应客户端请求消息  versionType -> {} , busiType -> {} , 耗时 -> {} 毫秒 ({} - {}) , params -> {} , results -> {}  ", versionType, busiType.getValue(),
                    (currTime - resultData.getSessionTime()), currTime, resultData.getSessionTime(), data, JSON.toJSONString(socketResult));*/
        }
    }

}
