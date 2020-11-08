package bean;

import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import thirdpart.fetchsurv.FetchService;
import thirdpart.order.OrderCmd;
import thirdpart.order.OrderDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * @BelongsProject: seq
 * @BelongsPackage: bean
 * @Author: Alone
 * @CreateTime: 2020-11-08 17:45
 * @Description: 定时抓取网关数据
 */
@Log4j2
@RequiredArgsConstructor
public class FetchTask extends TimerTask {

    @NonNull
    private SeqConfig config;


    @Override
    public void run() {
        //遍历网关
        if(!config.getNode().isLeader()) { //只在主节点获取数据
            return;
        }

        Map<String, FetchService> fetchServiceMap = config.getFetchServiceMap();
        if(MapUtils.isEmpty(fetchServiceMap)) {
            return;
        }

        //获取数据
        List<OrderCmd> cmds = collectAllOrders(fetchServiceMap);
        if(CollectionUtils.isEmpty(cmds)) {
            return;
        }
        log.info(cmds);
        // 对数据进行排序
        // 排序规则 时间优先 价格优先 量优先
        cmds.sort(((o1, o2) -> {
            int res = compareTime(o1, o2);
            if(res != 0)
                return res;
            res = comparePrice(o1, o2);
            if(res != 0)
                return res;
            res = compareVolume(o1, o2);
            return res;
        }));

        //存储到KV Store,发送到撮合核心
    }


    private List<OrderCmd> collectAllOrders(Map<String, FetchService> fetchServiceMap) {
        //不推荐 性能不可控 不利调试
//        List<OrderCmd> res = fetchServiceMap.values().stream()
//                .map(t -> t.fetchData())
//                .filter(msg -> CollectionUtils.isEmpty(msg))
//                .flatMap(List::stream)
//                .collect(Collectors.toList());
        //推荐
        List<OrderCmd> msgs = Lists.newArrayList();
        fetchServiceMap.values().forEach(t -> {
            List<OrderCmd> orderCmds = t.fetchData();
            if(CollectionUtils.isNotEmpty(orderCmds)) {
                msgs.addAll(orderCmds);
            }
        });
        return msgs;
    }

    private int compareTime(OrderCmd o1, OrderCmd o2) {
        if(o1.timestamp > o2.timestamp) {
            return 1;
        } else if(o1.timestamp < o2.timestamp) {
            return -1;
        } else {
            return 0;
        }
    }

    private int comparePrice(OrderCmd o1, OrderCmd o2) {
        if(o1.direction == o2.direction) {
            if(o1.price > o2.price) {
                return o1.direction == OrderDirection.BUY ? -1 : 1;
            } else if (o1.price < o2.price) {
                return o1.direction == OrderDirection.BUY ? 1 : -1;
            }
        }
        return 0;
    }

    private int compareVolume(OrderCmd o1, OrderCmd o2) {
        if(o1.volume > o2.volume)
            return -1;
        else if(o1.volume < o2.volume)
            return 1;
        else
            return 0;
    }
}
