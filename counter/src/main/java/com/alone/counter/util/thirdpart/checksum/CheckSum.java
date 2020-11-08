package com.alone.counter.util.thirdpart.checksum;

/**
 * @BelongsProject: gateway
 * @BelongsPackage: thirdpart.checksum
 * @Author: Alone
 * @CreateTime: 2020-11-01 15:54
 * @Description:
 */
public interface CheckSum {

    //将字节数组运算出一个字节
    byte getChecksum(byte[] data);
}
