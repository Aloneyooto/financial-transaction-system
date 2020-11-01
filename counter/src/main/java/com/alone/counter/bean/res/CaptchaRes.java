package com.alone.counter.bean.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.bean.res
 * @Author: Alone
 * @CreateTime: 2020-10-29 20:46
 * @Description: 验证码返回数据
 */
@AllArgsConstructor
@Data
@ToString
public class CaptchaRes {
    private String id;

    private String imageBase64;
}
