package com.alone.gateway.bean.handler;

import com.alone.gateway.bean.GatewayConfig;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import thirdpart.bean.CommonMsg;


/**
 * @BelongsProject: gateway
 * @BelongsPackage: com.alone.gateway.bean.handler
 * @Author: Alone
 * @CreateTime: 2020-11-01 20:45
 * @Description:
 */
@Log4j2
@RequiredArgsConstructor
public class ConnHandler implements Handler<NetSocket> {

    @NonNull
    private GatewayConfig config;

    private static final int PACKET_HEADER_LENGTH = 4 + 1 + 2 + 2 + 2 + 1 + 8;

    @Override
    public void handle(NetSocket netSocket) {

        MsgHandler msgHandler = new MsgHandlerImpl(config.getBodyCodec());
        msgHandler.onConnet(netSocket);

        //1.parser
        final RecordParser parser = RecordParser.newFixed(PACKET_HEADER_LENGTH);
        parser.setOutput(new Handler<Buffer>() {

            int bodyLength = -1;
            byte checksum = -1;
            short msgSrc = -1;
            short msgDst = -1;
            short msgType = -1;
            byte status = -1;
            long packetNo = -1;

            @Override
            public void handle(Buffer buffer) {
                if(bodyLength == -1) {
                    //读到包头
                    bodyLength = buffer.getInt(0);
                    checksum = buffer.getByte(4);
                    msgSrc = buffer.getShort(5);
                    msgDst = buffer.getShort(7);
                    msgType = buffer.getShort(9);
                    status = buffer.getByte(11);
                    packetNo = buffer.getLong(12);
                    parser.fixedSizeMode(bodyLength);
                } else {
                    //读取数据
                    byte[] bodyBytes = buffer.getBytes();
                    //组装对象
                    CommonMsg msg;
                    //验证校验和是否正确
                    if(checksum != config.getCs().getChecksum(bodyBytes)) {
                        log.error("illegal byte body exist from client:{}", netSocket.remoteAddress());
                        return;
                    } else {
                        if(msgDst != config.getId()) {
                            //包来错了地方
                            log.error("recv error msgDst dst:{} from client: {]", msgDst, netSocket.remoteAddress());
                            return;
                        }

                        //校验通过,组装对象
                        msg = new CommonMsg();
                        msg.setBodyLength(bodyBytes.length);
                        msg.setChecksum(checksum);
                        msg.setMsgSrc(msgSrc);
                        msg.setMsgType(msgType);
                        msg.setStatus(status);
                        msg.setMsgNo(packetNo);
                        msg.setBody(bodyBytes);
                        msg.setTimestamp(System.currentTimeMillis());

                        msgHandler.onCountData(msg);

                        //恢复现场
                        bodyLength = -1;
                        checksum = -1;
                        msgSrc = -1;
                        msgDst = -1;
                        msgType = -1;
                        status = -1;
                        packetNo = -1;
                        parser.fixedSizeMode(PACKET_HEADER_LENGTH);
                    }
                }
            }
        });
        netSocket.handler(parser);
        //2.异常 退出 处理器
        netSocket.closeHandler(close -> {
            msgHandler.onDisConnet(netSocket);
        });

        netSocket.exceptionHandler(e -> {
            msgHandler.onException(netSocket, e);
            netSocket.close();
        });
    }
}
