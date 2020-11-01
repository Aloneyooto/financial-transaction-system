package com.alone.counter.bean.res;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.bean.res
 * @Author: Alone
 * @CreateTime: 2020-10-31 11:02
 * @Description: 委托信息
 */
@Data
@NoArgsConstructor
@ToString
public class OrderInfo {

    private int id;

    private long uid;

    private int code;

    private String name;

    private int direction;

    private int type;

    private long price;

    private long ocount;

    private int status;

    private String date;

    private String time;
}
