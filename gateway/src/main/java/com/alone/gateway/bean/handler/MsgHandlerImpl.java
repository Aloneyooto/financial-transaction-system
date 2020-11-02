package com.alone.gateway.bean.handler;

import com.alone.gateway.bean.OrderCmdContainer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import thirdpart.bean.CommonMsg;
import thirdpart.codec.BodyCodec;
import thirdpart.order.OrderCmd;

/**
 * @BelongsProject: gateway
 * @BelongsPackage: com.alone.gateway.bean.handler
 * @Author: Alone
 * @CreateTime: 2020-11-02 14:31
 * @Description:
 */

@Log4j2
@AllArgsConstructor
public class MsgHandlerImpl implements MsgHandler {

    private BodyCodec bodyCodec;

    /**
     * 网关需要缓存数据，缓存动作必须异步
     * @param msg
     */
    @Override
    public void onCountData(CommonMsg msg) {
        OrderCmd orderCmd;
        try {
            orderCmd = bodyCodec.deserialize(msg.getBody(), OrderCmd.class);
            log.info("recv cmd: {}", orderCmd);
//            if(log.isDebugEnabled()) {
//                log.debug("recv cmd: {}", orderCmd);
//            }
            if(!OrderCmdContainer.getInstance().cache(orderCmd)) {
                log.error("gateway queue insert fail, queue length:{}, order: {}",
                        OrderCmdContainer.getInstance().size(),
                        orderCmd);
            }
        } catch (Exception e) {
            log.error("decode order cmd error", e);
        }
    }
}
