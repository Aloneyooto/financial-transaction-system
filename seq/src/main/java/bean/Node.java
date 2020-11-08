package bean;

import com.alipay.sofa.jraft.rhea.LeaderStateListener;
import com.alipay.sofa.jraft.rhea.client.DefaultRheaKVStore;
import com.alipay.sofa.jraft.rhea.client.RheaKVStore;
import com.alipay.sofa.jraft.rhea.options.RheaKVStoreOptions;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @BelongsProject: seq
 * @BelongsPackage: bean
 * @Author: Alone
 * @CreateTime: 2020-11-08 14:52
 * @Description: kv节点类
 */
@Log4j2
@RequiredArgsConstructor
public class Node {

    @Getter
    private RheaKVStore rheaKVStore;

    @NonNull
    private final RheaKVStoreOptions options;//封装对kvStore的操作

    private final AtomicLong leaderTerm = new AtomicLong(-1);//标志成员变量是否为leader

    /**
     * 判断是否为leader
     * @return
     */
    public boolean isLeader() {
        return leaderTerm.get() > 0;
    }

    /**
     * 启动节点
     */
    public void start() {
        //初始化kvstore
        rheaKVStore = new DefaultRheaKVStore();
        rheaKVStore.init(this.options);
        //监听节点状态
        rheaKVStore.addLeaderStateListener(-1, new LeaderStateListener() {
            /**
             * 当前节点已经成为leader节点
             * @param l
             */
            @Override
            public void onLeaderStart(long l) {
                log.info("node become leader");
                leaderTerm.set(l);
            }

            /**
             * 当前节点已经不是leader节点
             * @param l
             */
            @Override
            public void onLeaderStop(long l) {
                leaderTerm.set(-1);
            }
        });
    }


    /**
     * 停止节点
     */
    public void stop() {
        rheaKVStore.shutdown();
    }
}
