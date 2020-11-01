package com.alone.counter.cache;

import lombok.Getter;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.cache
 * @Author: Alone
 * @CreateTime: 2020-10-29 20:26
 * @Description: 缓存数据枚举类
 */
@Getter
public enum CacheType {
    CAPTCHA("captcha:"),
    ACCOUT("account:"),
    ORDER("order:"),
    TRADE("trade:"),
    POSI("posi:"),
    ;

    private String type;

    CacheType(String type) {
        this.type = type;
    }
}
