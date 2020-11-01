package com.alone.counter.service;

import com.alone.counter.bean.res.Account;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.service
 * @Author: Alone
 * @CreateTime: 2020-10-30 15:21
 * @Description:
 */
public interface AccountService {

    //login
    Account login(long uid, String password, String captcha, String captchaId) throws Exception;

    //缓存中是否存在已登录信息
    boolean accountExistInCache(String token);

    //退出登录
    boolean logout(String token);

    //修改密码
    boolean updatePwd(long uid, String oldPwd, String newPwd);
}
