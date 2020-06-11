package org.wjlmgqs.swp.core.wss.s;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 业务消息承载体，把消息内容放到msg，也可以集成该类指定字段，例如opensession对象
 */
@Getter
@Setter
@Accessors(chain = true)
public class WssSessionMsgData<R extends WssSessionMsgData> {

    /**
     * 客户端唯一标识
     */
    private String clientId;

}
