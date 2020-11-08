package com.alone.counter.util.thirdpart.bean;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @BelongsProject: gateway
 * @BelongsPackage: thirdpart.bean
 * @Author: Alone
 * @CreateTime: 2020-11-01 15:48
 * @Description: 网关内外沟通用消息体
 */

@Data
@ToString
public class CommonMsg implements Serializable {

    // 包头[包体长度 int + 校验和 byte + src short + dst short
    // + 消息类型 short + 消息状态 byte + 包编号 long]
    // 包体[ 数据 byte[] ]

    private int bodyLength;

    private byte checksum;

    private short msgSrc;

    private short msgDst;

    private short msgType;

    private byte status;

    private long msgNo;

    @ToString.Exclude
    private byte[] body;

    ////////////////////////////////
    private boolean isLegal;

    private short errCode;

    private long timestamp;
}
