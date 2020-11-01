package com.alone.counter.controller;

import com.alone.counter.bean.res.*;
import com.alone.counter.cache.StockCache;
import com.alone.counter.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.controller
 * @Author: Alone
 * @CreateTime: 2020-10-31 14:07
 * @Description:
 */
@RestController
@RequestMapping("/api")
@Log4j2
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockCache stockCache;

    @RequestMapping("/balance")
    public CounterRes balanceQuery(@RequestParam long uid) throws Exception {
        long balance = orderService.getBalance(uid);
        return new CounterRes(balance);
    }

    @RequestMapping("/posiinfo")
    public CounterRes posiQuery(@RequestParam long uid) throws Exception {
        List<PosiInfo> posiList = orderService.getPosiList(uid);
        return new CounterRes(posiList);
    }

    @RequestMapping("/orderinfo")
    public CounterRes orderQuery(@RequestParam long uid) throws Exception {
        List<OrderInfo> orderList = orderService.getOrderList(uid);
        return new CounterRes(orderList);
    }

    @RequestMapping("/tradeinfo")
    public CounterRes tradeQuery(@RequestParam long uid) throws Exception {
        List<TradeInfo> tradeList = orderService.getTradeList(uid);
        return new CounterRes(tradeList);
    }

    /**
     * 通过股票代码查询股票
     * @param key
     * @return
     */
    @RequestMapping("/stockquery")
    public CounterRes stockQuery(@RequestParam String key){
        Collection<StockInfo> stocks = stockCache.getStocks(key);
        return new CounterRes(stocks);
    }

    /**
     * 接收委托
     */
    @RequestMapping("/sendorder")
    public CounterRes sendOrder(
            @RequestParam int uid,
            @RequestParam short type,
            @RequestParam long timestamp,
            @RequestParam int code,
            @RequestParam byte direction,
            @RequestParam long price,
            @RequestParam long volume,
            @RequestParam byte ordertype
    ) {
        if(orderService.sendOrder(uid, type, timestamp, code, direction, price, volume, ordertype)) {
            return new CounterRes(CounterRes.SUCCESS, "save success", null);
        } else {
            return new CounterRes(CounterRes.FAIL, "save failed", null);
        }
    }
}
