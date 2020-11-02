package com.alone.gateway.bean.handler;

import io.vertx.core.net.NetSocket;
import thirdpart.bean.CommonMsg;

/**
 * @BelongsProject: gateway
 * @BelongsPackage: com.alone.gateway.bean.handler
 * @Author: Alone
 * @CreateTime: 2020-11-02 14:27
 * @Description:
 */
public interface MsgHandler {

    //连接消息
    default void onConnet(NetSocket socket){}

    //断开连接消息
    default void onDisConnet(NetSocket socket){}

    //异常消息
    default void onException(NetSocket socket, Throwable e){}

    //接收来自柜台的消息
    void onCountData(CommonMsg msg);
}
