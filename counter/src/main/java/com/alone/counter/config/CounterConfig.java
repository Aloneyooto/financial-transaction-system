package com.alone.counter.config;

import com.alone.counter.util.thirdpart.checksum.CheckSum;
import com.alone.counter.util.thirdpart.codec.BodyCodec;
import com.alone.counter.util.thirdpart.codec.MsgCodec;
import io.vertx.core.Vertx;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.config
 * @Author: Alone
 * @CreateTime: 2020-10-29 20:41
 * @Description: 配置bean
 */

@Getter
@Component
@Log4j2
public class CounterConfig {

    /////////////////////////////UUID相关配置/////////////////////////////////////

    @Value("${counter.dataCenterId}")
    private long dataCenterId;

    @Value("${counter.workerId}")
    private long workerId;

    /////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////网关配置//////////////////////////////////////

    @Value("${counter.sendip}")
    private String sendIp;

    @Value("${counter.sendport}")
    private int sendPort;

    @Value("${counter.gatewayid}")
    private short gatewayId;

    private Vertx vertx = Vertx.vertx();

    /////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////会员ID////////////////////////////////////////

    @Value("${counter.id}")
    private short id;

    ////////////////////////////////////////////////////////////////////////////


    ////////////////////////////编码相关配置//////////////////////////////////////

    @Value("${counter.checksum}")
    private String checkSumClass;

    @Value("${counter.bodycodec}")
    private String bodyCodecClass;

    @Value("${counter.msgcodec}")
    private String msgCodecClass;

    private CheckSum cs;

    private BodyCodec bodyCodec;

    private MsgCodec msgCodec;

    @PostConstruct
    private void init() {
        Class<?> clz;
        try {
            clz = Class.forName(checkSumClass);
            cs = (CheckSum) clz.getDeclaredConstructor().newInstance();

            clz = Class.forName(bodyCodecClass);
            bodyCodec = (BodyCodec) clz.getDeclaredConstructor().newInstance();

            clz = Class.forName(msgCodecClass);
            msgCodec = (MsgCodec) clz.getDeclaredConstructor().newInstance();

        } catch (ClassNotFoundException e) {
            log.error("init config error ", e);
        } catch (IllegalAccessException e) {
            log.error("init config error ", e);
        } catch (InstantiationException e) {
            log.error("init config error ", e);
        } catch (NoSuchMethodException e) {
            log.error("init config error ", e);
        } catch (InvocationTargetException e) {
            log.error("init config error ", e);
        }
    }
    ////////////////////////////////////////////////////////////////////////////
}
