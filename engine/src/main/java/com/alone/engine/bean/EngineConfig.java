package com.alone.engine.bean;

import com.alipay.sofa.jraft.rhea.client.DefaultRheaKVStore;
import com.alipay.sofa.jraft.rhea.client.RheaKVStore;
import com.alipay.sofa.jraft.rhea.options.PlacementDriverOptions;
import com.alipay.sofa.jraft.rhea.options.RegionRouteTableOptions;
import com.alipay.sofa.jraft.rhea.options.RheaKVStoreOptions;
import com.alipay.sofa.jraft.rhea.options.configured.MultiRegionRouteTableOptionsConfigured;
import com.alipay.sofa.jraft.rhea.options.configured.PlacementDriverOptionsConfigured;
import com.alipay.sofa.jraft.rhea.options.configured.RheaKVStoreOptionsConfigured;
import com.alone.engine.core.EngineApi;
import com.alone.engine.db.DbQuery;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import io.netty.handler.codec.CodecException;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbutils.QueryRunner;
import thirdpart.bean.CmdPack;
import thirdpart.checksum.CheckSum;
import thirdpart.codec.BodyCodec;
import thirdpart.codec.MsgCodec;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.*;

/**
 * @BelongsProject: engine
 * @BelongsPackage: bean
 * @Author: Alone
 * @CreateTime: 2020-11-09 15:31
 * @Description: 读取配置文件的类
 */
@Log4j2
@ToString
@Getter
@RequiredArgsConstructor
public class EngineConfig {

    private short id;

    private String orderRecvIp;

    private int orderRecvPort;

    private String seqUrlList;

    private String pubIp;

    private int pubPort;

    //配置文件地址
    @NonNull
    private String fileName;

    @NonNull
    private BodyCodec bodyCodec;

    @NonNull
    private CheckSum cs;

    @NonNull
    private MsgCodec msgCodec;

    private Vertx vertx = Vertx.vertx();

    //排队机的KvStore
    @Getter
    @ToString.Exclude
    private final RheaKVStore orderKvStore = new DefaultRheaKVStore();

    @Getter
    private EngineApi engineApi = new EngineApi();

    //数据库操作类
    @Getter
    private DbQuery db;

    /**
     * 程序入口
     */
    public void startUp() throws Exception{
        //1.读取配置文件
        initConfig();
        //2.数据库连接
        initDB();
        //3.启动撮合核心
//        startEngine();
        //4.建立总线连接 初始化数据的发送
//        initPub();
        //5.初始化接收排队机数据以及连接
        startSqeConn();
    }

    /**
     * 读取配置文件
     */
    private void initConfig() throws IOException {
        Properties properties = new Properties();
        properties.load(Object.class.getResourceAsStream("/" + fileName));

        id = Short.parseShort(properties.getProperty("id"));
        orderRecvIp = properties.getProperty("orderrecvip");
        orderRecvPort = Integer.parseInt(properties.getProperty("orderrecvport"));
        seqUrlList = properties.getProperty("sequrllist");
        pubIp = properties.getProperty("pubip");
        pubPort = Integer.parseInt(properties.getProperty("pubport"));

        log.info(this);
    }

    /**
     * 数据库查询
     */
    private void initDB() {
        QueryRunner runner = new QueryRunner(new ComboPooledDataSource());
        db = new DbQuery(runner);
    }

    /**
     * 连接排队机
     * 本质上是连接里面的KVStore
     */
    private void startSqeConn() {
        //路由表
        final List<RegionRouteTableOptions> regionRouteTableOptions
                = MultiRegionRouteTableOptionsConfigured
                .newConfigured()
                .withInitialServerList(-1L, seqUrlList)
                .config();

        final PlacementDriverOptions pdOpts = PlacementDriverOptionsConfigured
                .newConfigured()
                .withFake(true)
                .withRegionRouteTableOptionsList(regionRouteTableOptions)
                .config();

        final RheaKVStoreOptions opts = RheaKVStoreOptionsConfigured
                .newConfigured()
                .withPlacementDriverOptions(pdOpts)
                .config();

        orderKvStore.init(opts);

        ////////////////////////////////////////////////////////

        //委托指令处理器
        //放在缓存队列中处理
        CmdPacketQueue.getInstance().init(orderKvStore, bodyCodec, engineApi);

        //接收排队机的数据
        //组播 允许多个socket接收同一份数据
        DatagramSocket socket = vertx.createDatagramSocket(new DatagramSocketOptions());
        socket.listen(orderRecvPort, "0.0.0.0", asyncRes -> {
            if(asyncRes.succeeded()) {

                //处理接收数据
                socket.handler(packet -> {
                    Buffer udpData = packet.data();
                    if(udpData.length() > 0) {
                        try {
                            CmdPack cmdPack = bodyCodec.deserialize(udpData.getBytes(), CmdPack.class);
                            CmdPacketQueue.getInstance().cache(cmdPack);
                        } catch (CodecException e) {
                            log.error("decode packet error", e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        log.error("recv empty udp packet from client : {}", packet.sender().toString());
                    }
                });

                try {
                    //入参: 组播ip 网卡名字 发消息的udp源地址 异步处理器
                    socket.listenMulticastGroup(orderRecvIp,
                            mainInterface().getName(), null, asyncRes2 -> {
                                log.info("listen succeed {}", asyncRes2.succeeded());
                            });
                } catch (Exception e) {
                    log.error(e);
                }
            } else {
                log.error("Listen failed ,", asyncRes.cause());
            }
        });
    }

    /**
     * 获取唯一网卡名称
     * @return
     * @throws Exception
     */
    private static NetworkInterface mainInterface() throws Exception {
        final ArrayList<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        //过滤不符合要求的网卡
        final NetworkInterface networkInterface = interfaces.stream().filter(t -> {
            //1. !loopback
            //2.支持multicast
            //3.非虚拟机网卡
            //4.有IPV4
            try{
                final boolean isLoopback = t.isLoopback();
                final boolean supportMulticast = t.supportsMulticast();
                final boolean isVirtualBox = t.getDisplayName().contains("VirtualBox")
                        || t.getDisplayName().contains("Host-only");
                final boolean hasIpv4 = t.getInterfaceAddresses()
                        .stream().anyMatch(ia -> ia.getAddress() instanceof Inet4Address);
                return !isLoopback && supportMulticast && !isVirtualBox && hasIpv4;
            } catch (Exception e) {
                log.error("fine net interface error", e);
            }
            return false;
        }).sorted(Comparator.comparing(NetworkInterface::getName)).findFirst().orElse(null);
        return networkInterface;
    }
}
