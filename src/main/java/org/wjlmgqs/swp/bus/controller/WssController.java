package org.wjlmgqs.swp.bus.controller;

import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wjlmgqs.swp.bus.vo.WssCallerCallVo;
import org.wjlmgqs.swp.bus.wss.s.caller.CallerWssSessionServiceImpl;
import org.wjlmgqs.swp.bus.wss.s.caller.WssCallerCallParam;
import org.wjlmgqs.swp.core.wss.s.IWssSessionService;
import org.wjlmgqs.swp.core.wss.s.WssSessionMsg;

import javax.annotation.Resource;

/**
 * wss服务触发
 *
 * @author wjlmgqs@sina.com
 * @date 2019/2/12
 */
@RestController
@Slf4j
@RequestMapping("/web/wss")
public class WssController {

    @Autowired
    private DozerBeanMapper dozerBeanMapper;


    @Resource(name = "callerWssSessionServiceImpl")
    private IWssSessionService wssSessionService;

    /**
     * 叫号
     */
    @RequestMapping("/caller/call")
    public WssSessionMsg callerCall(@RequestBody WssCallerCallVo vo) {
        WssCallerCallParam callerCallParam = dozerBeanMapper.map(vo, WssCallerCallParam.class);
        WssSessionMsg wssSessionMsgData = ((CallerWssSessionServiceImpl) wssSessionService).sendBusiMsg(callerCallParam);
        return wssSessionMsgData;
    }


}


