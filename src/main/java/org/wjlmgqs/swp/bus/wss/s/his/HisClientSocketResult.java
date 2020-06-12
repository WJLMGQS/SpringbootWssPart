package org.wjlmgqs.swp.bus.wss.s.his;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.wjlmgqs.swp.core.wss.s.WssSessionMsgData;

/**
 * 响应客户端 - 客户端通信外壳
 *
 * @author wjlmgqs@sina.com
 * @date 2018/10/25 17:11
 */
@Setter
@Getter
@Accessors(chain = true)
public class HisClientSocketResult extends WssSessionMsgData<HisClientSocketResult> {

    /**
     * 结果对象
     */
    private Object result;

}
