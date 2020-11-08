package com.alone.counter.util.thirdpart;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.util.thirdpart
 * @Author: Alone
 * @CreateTime: 2020-11-02 21:09
 * @Description: 发送tcp的流
 */
@Log4j2
@RequiredArgsConstructor
public class TcpDirectSender {

    @NonNull
    private String ip;//接收方ip

    @NonNull
    private int port;//接收方端口

    @NonNull
    private Vertx vertx;

    ////////////////////////////////////////////////////////

    private volatile NetSocket socket;

    //缓存队列 socket自己去缓存中取数据进行发送
    private final BlockingQueue<Buffer> sendCache = new LinkedBlockingDeque<>();

    public boolean send(Buffer bufferMsg) {
        return sendCache.offer(bufferMsg);
    }

    /**
     * 创建客户端连接
     */
    public void startUp() {
        vertx.createNetClient().connect(port, ip, new ClientConnHandler());
        new Thread(() -> {
            while(true) {
               try {
                   Buffer msgBuffer = sendCache.poll(5, TimeUnit.SECONDS);
                   if(msgBuffer != null && msgBuffer.length() > 0 && socket != null) {
                       socket.write(msgBuffer);
                   }
               } catch (Exception e) {
                   log.error("msg send fail, continue");
               }
            }
        }).start();
    }

    /**
     * 连接处理器
     */
    private class ClientConnHandler implements Handler<AsyncResult<NetSocket>> {

        @Override
        public void handle(AsyncResult<NetSocket> result) {
            if(result.succeeded()) {
                log.info("connect success to remote {} : {}", ip, port);
                socket = result.result();
                socket.closeHandler(close -> {
                    log.info("connect to remote {} closed", socket.remoteAddress());
                    //重连
                    reconnect();
                });
                socket.exceptionHandler(ex -> {
                    log.error("error exist", ex.getCause());
                });
            } else {

            }
        }

        private void reconnect() {
            vertx.setTimer(1000 * 5, r -> {
                log.info("try reconnect to server to {} : {} failed", ip, port);
                vertx.createNetClient().connect(port, ip, new ClientConnHandler());
            });
        }
    }
}
