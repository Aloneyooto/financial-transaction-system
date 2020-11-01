package com.alone.counter.service;

import com.alone.counter.bean.res.OrderInfo;
import com.alone.counter.bean.res.PosiInfo;
import com.alone.counter.bean.res.TradeInfo;

import java.util.List;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.service
 * @Author: Alone
 * @CreateTime: 2020-10-31 14:02
 * @Description:
 */
public interface OrderService {

    //查资金
    Long getBalance(long uid);

    //查持仓
    List<PosiInfo> getPosiList(long uid);

    //查委托
    List<OrderInfo> getOrderList(long uid);

    //查成交
    List<TradeInfo> getTradeList(long uid);

    //发送委托
    boolean sendOrder(long uid, short type, long timestamp,
                      int code, byte direction, long price,
                      long volume, byte ordertype);
}
