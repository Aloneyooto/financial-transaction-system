package thirdpart.fetchsurv;

import thirdpart.order.OrderCmd;

import java.util.List;

/**
 * @BelongsProject: seq
 * @BelongsPackage: thirdpart.fetchsurv
 * @Author: Alone
 * @CreateTime: 2020-11-08 17:16
 * @Description: 网关和排队机通信的接口
 */
public interface FetchService {
    List<OrderCmd> fetchData();
}
