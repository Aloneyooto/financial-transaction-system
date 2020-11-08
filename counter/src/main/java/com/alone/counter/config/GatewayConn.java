package com.alone.counter.config;

import com.alone.counter.bean.order.OrderCmd;
import com.alone.counter.util.thirdpart.TcpDirectSender;
import com.alone.counter.util.thirdpart.bean.CommonMsg;
import com.alone.counter.util.thirdpart.uuid.GudyUuid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

import static com.alone.counter.util.thirdpart.bean.MsgConstants.COUNTER_NEW_ORDER;
import static com.alone.counter.util.thirdpart.bean.MsgConstants.NORMAL;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.config
 * @Author: Alone
 * @CreateTime: 2020-11-02 16:28
 * @Description:
 */
@Log4j2
@Configuration
public class GatewayConn {

    @Autowired
    private CounterConfig config;

    private TcpDirectSender directSender;

    @PostConstruct
    private void init() {
        directSender = new TcpDirectSender(config.getSendIp(), config.getSendPort(), config.getVertx());
        directSender.startUp();
    }

    /**
     * 发送委托
     * @param orderCmd
     */
    public void sendOrder(OrderCmd orderCmd) {
        byte[] data = null;
        try {
            data = config.getBodyCodec().serialize(orderCmd);
        } catch (Exception e) {
            log.error("encode error for ordercmd: {}", orderCmd, e);
            return;
        }

        CommonMsg msg = new CommonMsg();
        msg.setBodyLength(data.length);
        msg.setChecksum(config.getCs().getChecksum(data));
        msg.setMsgSrc(config.getId());
        msg.setMsgDst(config.getGatewayId());
        msg.setMsgType(COUNTER_NEW_ORDER);
        msg.setStatus(NORMAL);
        msg.setMsgNo(GudyUuid.getInstance().getUUID());
        msg.setBody(data);
        directSender.send(config.getMsgCodec().encodeToBuffer(msg));
    }

}
