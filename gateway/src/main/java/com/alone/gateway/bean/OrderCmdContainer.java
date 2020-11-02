package com.alone.gateway.bean;

import com.google.common.collect.Lists;
import thirdpart.order.OrderCmd;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @BelongsProject: gateway
 * @BelongsPackage: com.alone.gateway.bean
 * @Author: Alone
 * @CreateTime: 2020-11-02 14:35
 * @Description: 缓存
 */
public class OrderCmdContainer {

    //单例
    private static OrderCmdContainer ourinstance = new OrderCmdContainer();

    private OrderCmdContainer(){}

    public static OrderCmdContainer getInstance() {
        return ourinstance;
    }

    ////////////////////

    private final BlockingQueue<OrderCmd> queue = new LinkedBlockingDeque<>();

    public boolean cache(OrderCmd cmd) {
        return queue.offer(cmd);
    }

    public int size() {
        return queue.size();
    }

    public List<OrderCmd> getAll() {
        List<OrderCmd> msgList = Lists.newArrayList();
        //drainTo 不阻塞当前线程 把当前队列中的数据一次性取出来
        int count = queue.drainTo(msgList);
        if(count == 0) {
            return null;
        } else {
            return msgList;
        }
    }
}
