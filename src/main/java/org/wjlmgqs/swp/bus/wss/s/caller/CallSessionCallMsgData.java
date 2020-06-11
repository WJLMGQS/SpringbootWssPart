package org.wjlmgqs.swp.bus.wss.s.caller;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.wjlmgqs.swp.core.wss.s.WssSessionMsgData;

/**
 * 叫号记录对象
 * @author wengjialin@dxy.cn
 * @date 2018/10/25 17:11
 */
@Getter
@Setter
@Accessors(chain = true)
public class CallSessionCallMsgData extends WssSessionMsgData<CallSessionCallMsgData> {

    /**
     * 呼叫号码
     */
    private String callCode;

    /**
     * 患者ID
     */
    private String patientId;

    /**
     * 患者名称
     */
    private String patientName;

    /**
     * 患者手机号
     */
    private String patientPhone;

    /**
     * 医生id
     */
	private String doctorId;

    /**
     * 医生名称
     */
	private String doctorName;

    /**
     * 窗口ID
     */
	private String windowId;

}
