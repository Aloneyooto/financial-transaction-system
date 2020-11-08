package com.alone.gateway.bean;

import org.dom4j.DocumentException;
import thirdpart.checksum.ByteCheckSum;
import thirdpart.codec.BodyCodecImpl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @BelongsProject: gateway
 * @BelongsPackage: com.alone.gateway.bean
 * @Author: Alone
 * @CreateTime: 2020-11-03 00:08
 * @Description:
 */
public class GatewayStartup {

    public static void main(String[] args) throws DocumentException, UnsupportedEncodingException {
        //把网关配置类做个启动
        String configFileName = "gateway.xml";
        GatewayConfig config = new GatewayConfig();
        String url = GatewayStartup.class.getResource("/").getPath() + configFileName;
        String newUrl = URLDecoder.decode(url, "UTF-8");
        config.initConfig(newUrl);
        //把校验码工具类和序列化反序列化工具类设置进来
        config.setCs(new ByteCheckSum());
        config.setBodyCodec(new BodyCodecImpl());
        config.startup();
    }
}
