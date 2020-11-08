package com.alone.gateway.bean;

import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alone.gateway.bean.handler.ConnHandler;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import sun.security.provider.certpath.Vertex;
import thirdpart.checksum.CheckSum;
import thirdpart.codec.BodyCodec;
import thirdpart.fetchsurv.FetchService;

import java.io.File;

/**
 * @BelongsProject: gateway
 * @BelongsPackage: com.alone.gateway.bean
 * @Author: Alone
 * @CreateTime: 2020-11-01 20:00
 * @Description: 配置解析类
 */
@Log4j2
@Getter
public class GatewayConfig {
    //网关ID
    private short id;

    //端口
    private int recvPort;

    //排队机端口
    private int fetchServerPort;

    //TODO 柜台列表 数据库连接

    @Setter
    private BodyCodec bodyCodec;

    @Setter
    private CheckSum cs;

    private Vertx vertx = Vertx.vertx();

    /**
     * 初始化配置
     */
    public void initConfig(String fileName) throws DocumentException {
        //创建dom4j解析器
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(fileName));
        Element root = document.getRootElement();
        //1.端口
        id = Short.parseShort(root.element("id").getText());
        recvPort = Integer.parseInt(root.element("recvport").getText());
        fetchServerPort = Integer.parseInt(root.element("fetchserverport").getText());
        log.info("GateWay ID:{}, Port:{}, FetchServerPort : {}", id, recvPort, fetchServerPort);
        //TODO 数据库连接 连接柜台列表
    }

    public void startup() {
        //1.启动TCP服务监听
        initRecv();
        //2.排队机交互
        initFetchServer();
    }



    private void initRecv() {
        NetServer server = vertx.createNetServer();
        server.connectHandler(new ConnHandler(this));
        server.listen(recvPort, res -> {
            if(res.succeeded()) {
                log.info("gateway startup success at port: {}", recvPort);
            } else {
                log.error("gateway startup fail");
            }
        });
    }

    /**
     * 与排队机交互
     */
    private void initFetchServer() {
        ServerConfig rpcConfig = new ServerConfig()
                .setPort(fetchServerPort)
                .setProtocol("bolt");

        ProviderConfig<FetchService> providerConfig = new ProviderConfig<FetchService>()
                .setInterfaceId(FetchService.class.getName())
                .setRef(() -> OrderCmdContainer.getInstance().getAll())
                .setServer(rpcConfig);//绑定FetchService
        providerConfig.export();
        log.info("gateway startup fetchServer success at port : {}", fetchServerPort);
    }
}
