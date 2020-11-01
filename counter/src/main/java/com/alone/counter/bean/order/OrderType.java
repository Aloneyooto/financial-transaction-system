package com.alone.counter.bean.order;

import lombok.Getter;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.bean
 * @Author: Alone
 * @CreateTime: 2020-10-31 17:03
 * @Description:
 */
@Getter
public enum OrderType {
    LIMIT(0); // Immediate or Cancel - equivalent to strict-risk market order

    private byte type;

    OrderType(int type) {
        this.type = (byte) type;
    }

    public static OrderType of(byte type) {
        switch (type) {
            case 0:
                return LIMIT;
            default:
                throw new IllegalArgumentException("unknown OrderType:" + type);
        }
    }

}

