package com.alone.counter.bean.res;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.bean.res
 * @Author: Alone
 * @CreateTime: 2020-10-31 11:00
 * @Description: 持仓信息
 */
@Data
@NoArgsConstructor
@ToString
public class PosiInfo {

    private int id;

    private long uid;

    private int code;

    private String name;

    private long cost;

    private long count;
}
