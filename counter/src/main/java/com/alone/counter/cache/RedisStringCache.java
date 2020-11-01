package com.alone.counter.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.cache
 * @Author: Alone
 * @CreateTime: 2020-10-29 20:20
 * @Description: redis缓存封装类
 */
@Component
public class RedisStringCache {

    private static RedisStringCache redisStringCache;

    private RedisStringCache() {};

    @Autowired
    private StringRedisTemplate template;

    @Value("${cacheexpire.captcha}")
    private int captchaExpireTime;

    @Value("${cacheexpire.account}")
    private int accountExpireTime;

    @Value("${cacheexpire.order}")
    private int orderExpireTime;

    public static RedisStringCache getRedisStringCache() {
        return redisStringCache;
    }

    public static void setRedisStringCache(RedisStringCache redisStringCache) {
        RedisStringCache.redisStringCache = redisStringCache;
    }

    public StringRedisTemplate getTemplate() {
        return template;
    }

    public void setTemplate(StringRedisTemplate template) {
        this.template = template;
    }

    public int getCaptchaExpireTime() {
        return captchaExpireTime;
    }

    public void setCaptchaExpireTime(int captchaExpireTime) {
        this.captchaExpireTime = captchaExpireTime;
    }

    public int getAccountExpireTime() {
        return accountExpireTime;
    }

    public void setAccountExpireTime(int accountExpireTime) {
        this.accountExpireTime = accountExpireTime;
    }

    public int getOrderExpireTime() {
        return orderExpireTime;
    }

    public void setOrderExpireTime(int orderExpireTime) {
        this.orderExpireTime = orderExpireTime;
    }

    @PostConstruct
    private void init() {
        redisStringCache = new RedisStringCache();
        redisStringCache.setTemplate(template);
        redisStringCache.setAccountExpireTime(accountExpireTime);
        redisStringCache.setCaptchaExpireTime(captchaExpireTime);
        redisStringCache.setOrderExpireTime(orderExpireTime);
    }

    //增加缓存
    public static void cache(String key, String value, CacheType cacheType) {
        int expireTime;
        switch (cacheType) {
            case ACCOUT:
                expireTime = redisStringCache.getAccountExpireTime();
                break;
            case CAPTCHA:
                expireTime = redisStringCache.getCaptchaExpireTime();
                break;
            case ORDER:
            case TRADE:
            case POSI:
                expireTime = redisStringCache.getOrderExpireTime();
                break;
            default:
                expireTime = 10;
                break;//?
        }
        redisStringCache.getTemplate()
                .opsForValue().set(cacheType.getType() + key, value, expireTime, TimeUnit.SECONDS);
    }
    //删除缓存
    public static void remove(String key, CacheType cacheType) {
        redisStringCache.getTemplate()
                .delete(cacheType.getType() + key);
    }
    //查询缓存
    public static String get(String key, CacheType cacheType) {
        return redisStringCache.getTemplate()
                .opsForValue().get(cacheType.getType() + key);
    }
}
