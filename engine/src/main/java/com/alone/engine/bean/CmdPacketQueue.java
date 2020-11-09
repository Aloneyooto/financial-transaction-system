package com.alone.engine.bean;

import com.alipay.sofa.jraft.rhea.client.RheaKVStore;
import com.alipay.sofa.jraft.rhea.storage.KVEntry;
import com.alipay.sofa.jraft.util.Bits;
import com.google.common.collect.Lists;
import com.alone.engine.core.EngineApi;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import thirdpart.bean.CmdPack;
import thirdpart.codec.BodyCodec;
import thirdpart.order.OrderCmd;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: engine
 * @BelongsPackage: bean
 * @Author: Alone
 * @CreateTime: 2020-11-09 15:50
 * @Description: 排队机发过来的数据的缓存队列
 */
@Log4j2
public class CmdPacketQueue {

    private static CmdPacketQueue ourInstance = new CmdPacketQueue();

    private CmdPacketQueue() {
    }

    public static CmdPacketQueue getInstance() {
        return ourInstance;
    }

    /////////////////////////////////////////////////////////

    //委托队列
    private final BlockingQueue<CmdPack> recvCache = new LinkedBlockingDeque<>();

    /**
     * 将数据放入队列的方法
     * @param pack
     */
    public void cache(CmdPack pack) {
        recvCache.offer(pack);
    }

    /////////////////////////////////////////////////////////

    private RheaKVStore orderKVStore;

    private BodyCodec codec;

    private EngineApi engineApi;

    private long lastPackNo = -1;

    /**
     * 处理数据的方法
     */
    public void init(RheaKVStore orderKVStore, BodyCodec codec, EngineApi engineApi) {
        this.orderKVStore = orderKVStore;
        this.codec = codec;
        this.engineApi = engineApi;

        new Thread(() -> {
            while(true) {
                try {
                    CmdPack cmds = recvCache.poll(10, TimeUnit.SECONDS);
                    if(cmds != null) {
                        handle(cmds);
                    }
                } catch (Exception e) {
                    log.error("msg packet recvcache error, continue", e);
                }
            }
        }).start();
    }

    private void handle(CmdPack cmdPack) throws Exception {
        log.info("recv : {}", cmdPack);

        //对数据包进行NACK校验
        long packNo = cmdPack.getPackNo();
        if(packNo == lastPackNo + 1) {
            //正确 未丢包
            if(CollectionUtils.isEmpty(cmdPack.getOrderCmds())) {
                return;
            }
            for (OrderCmd cmd : cmdPack.getOrderCmds()) {
                engineApi.submitCommand(cmd);
            }
        } else if(packNo <= lastPackNo) {
            //来自历史的重复包
            log.warn("recv duplicate packId: {}", packNo);
        } else {
            //中间跳号了
            log.info("packNo lost from {} to {}, begin query from sequencer", lastPackNo + 1, packNo);
            //请求数据
            byte[] firstKey = new byte[8];
            Bits.putLong(firstKey, 0, lastPackNo + 1); //左端点
            byte[] lastKey = new byte[8];
            Bits.putLong(lastKey, 0, packNo + 1);
            //补上缺失的数据
            final List<KVEntry> kvEntries = orderKVStore.bScan(firstKey, lastKey);
            if(CollectionUtils.isNotEmpty(kvEntries)) {
                List<CmdPack> collect = Lists.newArrayList();
                for (KVEntry entry : kvEntries) {
                    byte[] value = entry.getValue();
                    if(ArrayUtils.isNotEmpty(value)) {
                        collect.add(codec.deserialize(value, CmdPack.class));
                    }
                }
                collect.sort(((o1, o2) -> (int)(o1.getPackNo() - o2.getPackNo())));
                for (CmdPack pack : collect) {
                    if(CollectionUtils.isEmpty(pack.getOrderCmds())) {
                        continue;
                    }
                    for (OrderCmd cmd : pack.getOrderCmds()) {
                        engineApi.submitCommand(cmd);
                    }
                }
            }
            //没有进入if表示排队机出错导致跳号
            lastPackNo = packNo;
        }
    }
}
