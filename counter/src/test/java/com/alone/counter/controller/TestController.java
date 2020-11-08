package com.alone.counter.controller;

import com.alone.counter.bean.res.CaptchaRes;
import com.alone.counter.cache.CacheType;
import com.alone.counter.cache.RedisStringCache;
import com.alone.counter.util.Captcha;
import com.alone.counter.util.IDConverter;
import com.alone.counter.util.TimeformatUtil;
import com.alone.counter.util.thirdpart.uuid.GudyUuid;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.controller
 * @Author: Alone
 * @CreateTime: 2020-10-29 21:14
 * @Description:
 */
public class TestController {

    @Test
    public void captchaTest() throws IOException {
        //1.生成验证码 120 * 40
        Captcha captcha = new Captcha(120, 140, 4, 10);
        //2.将验证码（ID，数值）放入缓存
        String uuid = String.valueOf(GudyUuid.getInstance().getUUID());
        RedisStringCache.cache(uuid, captcha.getCode(), CacheType.CAPTCHA);
        //3.使用base64编码图片,并返回给前台
        //uuid,base64图片
        CaptchaRes res = new CaptchaRes(uuid, captcha.getBase64ByteStr());
        System.out.println(res.getImageBase64());
    }

    @Test
    public void generateDate() {
        Date date = new Date();
        System.out.println(TimeformatUtil.yyyyMMddHHmmss(date));
    }

    @Test
    public void testIDConverter() {
        int high = 1001;
        int low = 200;
        long l = IDConverter.combineInt2Long(high, low);
        System.out.println(l);
        int[] ints = IDConverter.seperateLong2Int(l);
        System.out.println(ints[0]);
        System.out.println(ints[1]);
    }
}
