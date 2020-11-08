package com.alone.counter.util.thirdpart.codec;

import com.alipay.remoting.serialization.SerializerManager;

/**
 * @BelongsProject: gateway
 * @BelongsPackage: thirdpart.codec
 * @Author: Alone
 * @CreateTime: 2020-11-01 18:22
 * @Description: 序列化接口实现
 */
public class BodyCodecImpl implements BodyCodec {

    public <T> byte[] serialize(T obj) throws Exception {
        //1.jdk 序列化
        //2.json
        //3.自定义算法(Hessian2加密算法)
        return SerializerManager.getSerializer(SerializerManager.Hessian2).serialize(obj);
    }

    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        return SerializerManager.getSerializer(SerializerManager.Hessian2).deserialize(bytes, clazz.getName());
    }
}
