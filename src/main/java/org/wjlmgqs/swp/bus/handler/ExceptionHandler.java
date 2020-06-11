package org.wjlmgqs.swp.bus.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.wjlmgqs.swp.core.config.PropertiesConfig;
import org.wjlmgqs.swp.core.exps.CustomizedException;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常处理器
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(CustomizedException.class)
    public Map<String, Object> handleException(CustomizedException e) {
        log.error("code -> {} , msg -> {} , err -> {} " , e.getCode() , e.getMsg() , e);
        String msg = PropertiesConfig.getContextProperty(e.getCode(), e.getMsg());
        Map<String, Object> result = new HashMap<>();
        result.put("code", e.getCode());
        result.put("msg", msg);    //获取异常信息
        return result;
    }


}
