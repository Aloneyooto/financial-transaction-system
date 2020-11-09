package com.alone.engine;

import com.alone.engine.bean.EngineConfig;
import thirdpart.checksum.ByteCheckSum;
import thirdpart.codec.BodyCodecImpl;
import thirdpart.codec.MsgCodecImpl;

/**
 * @BelongsProject: engine
 * @BelongsPackage: com.alone.engine
 * @Author: Alone
 * @CreateTime: 2020-11-09 16:46
 * @Description:
 */
public class EngineStartUp {

    public static void main(String[] args) throws Exception {
        new EngineConfig(
                "engine.properties",
                new BodyCodecImpl(),
                new ByteCheckSum(),
                new MsgCodecImpl()
        ).startUp();
    }
}
