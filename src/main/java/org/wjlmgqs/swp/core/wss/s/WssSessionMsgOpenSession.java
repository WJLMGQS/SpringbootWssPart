package org.wjlmgqs.swp.core.wss.s;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 加入会话
 */
@Getter
@Setter
@Accessors(chain = true)
public class WssSessionMsgOpenSession<T extends WssSessionMsgOpenSession> extends WssSessionMsgData<T> {


}
