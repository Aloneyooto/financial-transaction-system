package com.alone.counter.util.thirdpart.checksum;

/**
 * @BelongsProject: gateway
 * @BelongsPackage: thirdpart.checksum
 * @Author: Alone
 * @CreateTime: 2020-11-01 15:56
 * @Description:
 */
public class ByteCheckSum implements CheckSum {

    /**
     * 计算校验和
     * @param data
     * @return
     */
    public byte getChecksum(byte[] data) {
        byte sum = 0;
        //把所有字节进行异或累加
        for(byte b: data) {
            sum ^= b;
        }
        return sum;
    }
}
