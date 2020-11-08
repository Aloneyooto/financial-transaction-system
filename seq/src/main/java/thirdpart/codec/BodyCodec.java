package thirdpart.codec;

/**
 * @BelongsProject: gateway
 * @BelongsPackage: thirdpart.codec
 * @Author: Alone
 * @CreateTime: 2020-11-01 16:01
 * @Description:
 */
public interface BodyCodec {

    //obj --> byte[]
    <T>byte[] serialize(T obj) throws Exception;

    //byte[] --> obj
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception;
}
