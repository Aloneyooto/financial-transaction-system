package com.alone.counter.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.config
 * @Author: Alone
 * @CreateTime: 2020-10-29 20:41
 * @Description: 配置bean
 */

@Getter
@Component
public class CounterConfig {

    /////////////////////////////UUID相关配置/////////////////////////////////////

    @Value("${counter.dataCenterId}")
    private long dataCenterId;

    @Value("${counter.workerId}")
    private long workerId;

    ////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////会员ID///////////////////////////////////////

    @Value("${counter.id}")
    private short id;

    ///////////////////////////////////////////////////////////////////////////
}
