package com.alone.counter.bean.res;

import lombok.*;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.bean.res
 * @Author: Alone
 * @CreateTime: 2020-10-30 15:03
 * @Description: 账户类
 */
@Data
@ToString
@NoArgsConstructor
@RequiredArgsConstructor //生成一个含有NonNull注解的构造函数
public class Account {

    @NonNull
    private int id;

    @NonNull
    private long uid;

    @NonNull
    private String lastLoginDate;

    @NonNull
    private String lastLoginTime;

    private String token;
}
