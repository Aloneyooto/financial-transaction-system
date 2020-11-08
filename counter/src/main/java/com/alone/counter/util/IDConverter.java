package com.alone.counter.util;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.util
 * @Author: Alone
 * @CreateTime: 2020-11-02 15:38
 * @Description: ID转换器
 */
public class IDConverter {

    /**
     * 把两个整型拼成一个长整型
     * @param high
     * @param low
     * @return
     */
    public static long combineInt2Long(int high, int low) {
        return ((long) high << 32 & 0xFFFFFFFF00000000L) | ((long) low & 0xFFFFFFFFL);
    }

    /**
     * 把一个长整型拆分成两个短整型
     * @param val
     * @return
     */
    public static int[] seperateLong2Int(long val) {
        int[] res = new int[2];
        res[1] = (int) (0xFFFFFFFFL & val);//低位
        res[0] = (int) ((0xFFFFFFFF00000000L & val) >> 32);//高位
        return res;
    }
}
