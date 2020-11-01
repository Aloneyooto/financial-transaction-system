package com.alone.counter.bean.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.bean.res
 * @Author: Alone
 * @CreateTime: 2020-10-29 19:26
 * @Description: 通用返回结果的javabean
 */
@Data
@AllArgsConstructor
public class CounterRes {

    public static final int SUCCESS = 0;

    public static final int RELOGIN = 1;

    public static final int FAIL = 2;

    private int code;

    private String message;

    private Object data;

    public CounterRes(Object data) {
        this(0, "", data);
    }
}
