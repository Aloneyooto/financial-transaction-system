package com.alone.counter.service.impl;

import com.alone.counter.bean.order.CmdType;
import com.alone.counter.bean.order.OrderCmd;
import com.alone.counter.bean.order.OrderDirection;
import com.alone.counter.bean.order.OrderType;
import com.alone.counter.bean.res.OrderInfo;
import com.alone.counter.bean.res.PosiInfo;
import com.alone.counter.bean.res.TradeInfo;
import com.alone.counter.config.CounterConfig;
import com.alone.counter.service.OrderService;
import com.alone.counter.util.DbUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.service.impl
 * @Author: Alone
 * @CreateTime: 2020-10-31 14:05
 * @Description:
 */
@Log4j2
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CounterConfig config;

    @Override
    public Long getBalance(long uid) {
        return DbUtil.getBalance(uid);
    }

    @Override
    public List<PosiInfo> getPosiList(long uid) {
        return DbUtil.getPosiList(uid);
    }

    @Override
    public List<OrderInfo> getOrderList(long uid) {
        return DbUtil.getOrderList(uid);
    }

    @Override
    public List<TradeInfo> getTradeList(long uid) {
        return DbUtil.getTradeList(uid);
    }

    @Override
    public boolean sendOrder(long uid, short type, long timestamp, int code, byte direction, long price, long volume, byte ordertype) {
        final OrderCmd orderCmd = OrderCmd.builder()
                .type(CmdType.of(type))
                .timestamp(timestamp)
                .mid(config.getId())
                .uid(uid)
                .code(code)
                .direction(OrderDirection.of(ordertype))
                .price(price)
                .volume(volume)
                .orderType(OrderType.of(ordertype))
                .build();

        //1.入库
        int oid = DbUtil.saveOrder(orderCmd);
        if(oid < 0) {
            return false;
        } else {
            //TODO 发送网关
            log.info(orderCmd);
            return true;
        }
    }
}
