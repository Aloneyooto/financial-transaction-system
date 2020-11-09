package bean;

import com.alipay.sofa.jraft.rhea.options.PlacementDriverOptions;
import com.alipay.sofa.jraft.rhea.options.RheaKVStoreOptions;
import com.alipay.sofa.jraft.rhea.options.StoreEngineOptions;
import com.alipay.sofa.jraft.rhea.options.configured.MemoryDBOptionsConfigured;
import com.alipay.sofa.jraft.rhea.options.configured.PlacementDriverOptionsConfigured;
import com.alipay.sofa.jraft.rhea.options.configured.RheaKVStoreOptionsConfigured;
import com.alipay.sofa.jraft.rhea.options.configured.StoreEngineOptionsConfigured;
import com.alipay.sofa.jraft.rhea.storage.StorageType;
import com.alipay.sofa.jraft.util.Endpoint;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.listener.ChannelListener;
import com.alipay.sofa.rpc.transport.AbstractChannel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.vertx.core.Vertx;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import thirdpart.codec.BodyCodec;
import thirdpart.fetchsurv.FetchService;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;

/**
 * @BelongsProject: seq
 * @BelongsPackage: bean
 * @Author: Alone
 * @CreateTime: 2020-11-08 15:06
 * @Description: 排队机配置类
 */
@Log4j2
@ToString
@RequiredArgsConstructor
public class SeqConfig {

    //raft文件的本地路径
    private String dataPath;

    //当前在哪个ip及端口进行服务
    private String serverUrl;

    //当前kvstore集群的节点列表
    private String serverList;

    @NonNull
    private String fileName;

    @Getter
    private Node node;

    private String fetchUrls;

    //与下游通信的链接
    @ToString.Exclude
    @Getter
    private Map<String, FetchService> fetchServiceMap = Maps.newConcurrentMap();

    //解包
    @NonNull
    @ToString.Exclude
    @Getter
    private BodyCodec codec;

    //与撮合核心交互的地址
    @Getter
    private String multicastIp;

    //与撮合核心交互的端口
    @Getter
    private int multicastPort;

    //发送udp包的变量
    @Getter
    private DatagramSocket multicastSender;

    /**
     * 启动
     */
    public void startUp() throws Exception{

        //1.读取配置文件
        initConfig();

        //2.初始化集群
        startSeqDbCluster();

        //3.启动下游广播
        startMultiCast();

        //4.初始化网关链接
        startupFetch();
    }


    /**
     * 读取配置文件
     */
    private void initConfig() throws IOException {
        //创建加载配置文件的流
        Properties properties = new Properties();
        properties.load(Object.class.getResourceAsStream("/" + fileName));

        //获取属性
        dataPath = properties.getProperty("datapath");
        dataPath = URLDecoder.decode(dataPath, "UTF-8");
        serverUrl = properties.getProperty("serverurl");
        serverList = properties.getProperty("serverlist");
        fetchUrls = properties.getProperty("fetchurls");
        multicastIp = properties.getProperty("multicastip");
        multicastPort = Integer.valueOf(properties.getProperty("multicastport"));

        log.info("read config : {}", this);
    }

    /**
     * 启动KV Store
     */
    private void startSeqDbCluster() {
        //
        final PlacementDriverOptions pdOpts = PlacementDriverOptionsConfigured.newConfigured()
                .withFake(true)
                .config();
        String[] split = serverUrl.split(":");
        final StoreEngineOptions storeOpts = StoreEngineOptionsConfigured.newConfigured()
                .withStorageType(StorageType.Memory)
                .withMemoryDBOptions(MemoryDBOptionsConfigured.newConfigured().config())
                .withRaftDataPath(dataPath)
                .withServerAddress(new Endpoint(split[0], Integer.parseInt(split[1])))
                .config();

        final RheaKVStoreOptions opts = RheaKVStoreOptionsConfigured.newConfigured()
                .withInitialServerList(serverList)
                .withStoreEngineOptions(storeOpts)
                .withPlacementDriverOptions(pdOpts)
                .config();

        node = new Node(opts);
        //启动节点
        node.start();
        //将节点的关闭挂载到jdk shutdown的流程中
        Runtime.getRuntime().addShutdownHook(new Thread(node::stop));
        log.info("start seq node success on port : {}", split[1]);
    }

    /**
     * 初始化网关连接
     * 抓取逻辑：
     * 1.从哪些网关抓取
     * 2.通信方式
     */
    private void startupFetch() {
        //1.建立所有到网关的连接
        String[] urls = fetchUrls.split(";");
        //对每个地址建立连接
        for (String url : urls) {
            ConsumerConfig<FetchService> consumerConfig = new ConsumerConfig<FetchService>()
                    .setInterfaceId(FetchService.class.getName()) //通信接口
                    .setProtocol("bolt") //RPC通信协议
                    .setTimeout(5000) //超时时间
                    .setDirectUrl(url); //直连地址
            consumerConfig.setOnConnect(Lists.newArrayList(new FetchChannelListener(consumerConfig)));
            //第一次建立连接不会放进去
            fetchServiceMap.put(url, consumerConfig.refer());
        }
        //2.定时抓取数据的任务
        new Timer().schedule(new FetchTask(this), 5000, 1000);
    }

    @RequiredArgsConstructor
    private class FetchChannelListener implements ChannelListener {

        @NonNull
        private ConsumerConfig<FetchService> config;

        @Override
        public void onConnected(AbstractChannel channel) {
            String remoteAddr = channel.remoteAddress().toString();
            log.info("connect to gateway : {}", remoteAddr);
            fetchServiceMap.put(remoteAddr, config.refer());
        }

        @Override
        public void onDisconnected(AbstractChannel channel) {
            String remoteAddr = channel.remoteAddress().toString();
            log.info("disconnect from gateway : {}", remoteAddr);
            fetchServiceMap.remove(remoteAddr);
        }
    }

    /**
     * 发送广播到撮合核心
     */
    private void startMultiCast() {
        multicastSender = Vertx.vertx().createDatagramSocket(new DatagramSocketOptions());
    }

}
