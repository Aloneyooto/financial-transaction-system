package com.alone.counter.util.thirdpart.codec;

import com.alone.counter.util.thirdpart.bean.CommonMsg;
import io.vertx.core.buffer.Buffer;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.util.thirdpart.codec
 * @Author: Alone
 * @CreateTime: 2020-11-02 20:17
 * @Description: 实现TCP数据流和CommonMsg模板类的相互转换
 */
public interface MsgCodec {

    //TCP <--> CommonMsg
    Buffer encodeToBuffer(CommonMsg msg);

    CommonMsg decodeFromBuffer(Buffer buffer);
}
