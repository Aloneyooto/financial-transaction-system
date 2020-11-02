import thirdpart.checksum.ByteCheckSum;
import thirdpart.codec.BodyCodecImpl;

import java.io.Serializable;

/**
 * @BelongsProject: gateway
 * @BelongsPackage: PACKAGE_NAME
 * @Author: Alone
 * @CreateTime: 2020-11-01 19:44
 * @Description:
 */
public class Test {

    static class A implements Serializable {
        public String a;
    }

    /**
     * 测试序列化
     * @throws Exception
     */
    @org.junit.Test
    public void test1() throws Exception {
        A a = new A();
        a.a = "test";

        byte[] serialize = new BodyCodecImpl().serialize(a);
        A deserialize = new BodyCodecImpl().deserialize(serialize, A.class);
        System.out.println(deserialize.a);
    }

    /**
     * 测试校验和
     */
    @org.junit.Test
    public void test2() {
        String a = "test";
        String b = "test1";
        String c = "test";
        byte ca = new ByteCheckSum().getChecksum(a.getBytes());
        byte cb = new ByteCheckSum().getChecksum(b.getBytes());
        byte cc = new ByteCheckSum().getChecksum(c.getBytes());
        System.out.println(ca);
        System.out.println(cb);
        System.out.println(cc);
    }
}
