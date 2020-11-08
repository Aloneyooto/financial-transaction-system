package com.alone.counter.service.impl;

import com.alone.counter.bean.res.Account;
import com.alone.counter.cache.CacheType;
import com.alone.counter.cache.RedisStringCache;
import com.alone.counter.service.AccountService;
import com.alone.counter.util.DbUtil;
import com.alone.counter.util.JsonUtil;
import com.alone.counter.util.TimeformatUtil;
import com.alone.counter.util.thirdpart.uuid.GudyUuid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.service.impl
 * @Author: Alone
 * @CreateTime: 2020-10-30 16:17
 * @Description:
 */
@Service
public class AccoutServiceImpl implements AccountService {

    /**
     * 登录
     * @param uid
     * @param password
     * @param captcha
     * @param captchaId
     * @return
     * @throws Exception
     */
    @Override
    public Account login(long uid, String password, String captcha, String captchaId) throws Exception {
        //1.入参的合法性校验
        if(StringUtils.isAnyBlank(password, captcha, captchaId)) {
            return null;
        }
        //2.校验缓存验证码
        String captchaCache = RedisStringCache.get(captchaId, CacheType.CAPTCHA);
        if(StringUtils.isEmpty(captchaCache)) {
            return null;
        } else if(!StringUtils.equalsIgnoreCase(captcha, captchaCache)) {
            return null;
        }
        //校验成功,删除缓存
        RedisStringCache.remove(captchaId, CacheType.CAPTCHA);
        //3.比对数据库用户名和密码
        Account account = DbUtil.queryAccount(uid, password);
        if(account == null) {
            return null;
        } else {
            //生成并设置唯一token
            account.setToken(String.valueOf(
                    GudyUuid.getInstance().getUUID()
            ));
            //存入缓存
            RedisStringCache.cache(String.valueOf(account.getToken()), JsonUtil.toJson(account), CacheType.ACCOUT);
            //更新登录时间
            Date date = new Date();
            DbUtil.updateLoginTime(uid, TimeformatUtil.yyyyMMdd(date), TimeformatUtil.hhMMss(date));
            return account;
        }
    }

    @Override
    public boolean accountExistInCache(String token) {
        if(StringUtils.isBlank(token)) {
            return false;
        }
        //从缓存获取数据
        String acc = RedisStringCache.get(token, CacheType.ACCOUT);
        if(acc != null) {
            RedisStringCache.cache(token, acc, CacheType.ACCOUT);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 退出登录要做的事情就是把缓存中的token清除
     * @param token
     * @return
     */
    @Override
    public boolean logout(String token) {
        RedisStringCache.remove(token, CacheType.ACCOUT);
        return true;
    }

    /**
     * 修改密码
     * @param uid id
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @return
     */
    @Override
    public boolean updatePwd(long uid, String oldPwd, String newPwd) {
        int res = DbUtil.updatePwd(uid, oldPwd, newPwd);
        return res == 0 ? false : true;
    }
}
