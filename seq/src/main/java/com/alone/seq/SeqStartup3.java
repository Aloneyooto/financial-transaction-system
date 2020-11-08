package com.alone.seq;

import bean.SeqConfig;
import thirdpart.codec.BodyCodecImpl;

/**
 * @BelongsProject: seq
 * @BelongsPackage: com.alone.seq
 * @Author: Alone
 * @CreateTime: 2020-11-08 17:04
 * @Description:
 */
public class SeqStartup3 {
    public static void main(String[] args) throws Exception {
        String configName = "seq3.properties";
        new SeqConfig(configName, new BodyCodecImpl()).startUp();
    }
}
