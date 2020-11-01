package com.alone.counter.controller;

import com.alone.counter.bean.res.CounterRes;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.controller
 * @Author: Alone
 * @CreateTime: 2020-10-31 17:52
 * @Description: 全局异常处理
 */
@ControllerAdvice
@ResponseBody
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public CounterRes exceptionHandler(HttpServletRequest request, Exception e) {
        log.error(e);
        return new CounterRes(CounterRes.FAIL,
                "发生错误",
                null);
    }
}
