package org.wjlmgqs.swp.bus.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 前端页面触发叫号对象
 */
@Getter
@Setter
@Accessors(chain = true)
public class WssCallerCallVo  {

    /**
     * 叫号诊所id
     */
    private Integer clinicId;

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
