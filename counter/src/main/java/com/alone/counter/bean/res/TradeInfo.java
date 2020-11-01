package com.alone.counter.bean.res;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.bean.res
 * @Author: Alone
 * @CreateTime: 2020-10-31 11:05
 * @Description: 成交信息
 */
@Data
@NoArgsConstructor
@ToString
public class TradeInfo {

    private int id;

    private long uid;

    private int code;

    private String name;

    private int direction;

    private long price;

    private long tcount;

    private int oid;
}
