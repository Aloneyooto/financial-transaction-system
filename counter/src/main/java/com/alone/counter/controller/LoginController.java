package com.alone.counter.controller;

import com.alone.counter.bean.res.Account;
import com.alone.counter.bean.res.CaptchaRes;
import com.alone.counter.bean.res.CounterRes;
import com.alone.counter.cache.CacheType;
import com.alone.counter.cache.RedisStringCache;
import com.alone.counter.service.AccountService;
import com.alone.counter.util.Captcha;
import com.alone.counter.util.thirdpart.uuid.GudyUuid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.alone.counter.bean.res.CounterRes.*;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.controller
 * @Author: Alone
 * @CreateTime: 2020-10-29 19:25
 * @Description: 登录控制器
 */
@RestController
@RequestMapping("/login")
@Log4j2
public class LoginController {

    @Autowired
    private AccountService accountService;

    /**
     * 验证码请求接口
     */
    @RequestMapping("/captcha")
    public CounterRes captcha() throws Exception {
        //1.生成验证码 120 * 40
        Captcha captcha = new Captcha(120, 40, 4, 10);
        //2.将验证码（ID，数值）放入缓存
        String uuid = String.valueOf(GudyUuid.getInstance().getUUID());
        RedisStringCache.cache(uuid, captcha.getCode(), CacheType.CAPTCHA);
        //3.使用base64编码图片,并返回给前台
        //uuid,base64图片
        CaptchaRes res = new CaptchaRes(uuid, captcha.getBase64ByteStr());
        return new CounterRes(res);
    }

    /**
     * 登录请求接口
     * 调用关系: LoginController -> AccountService -> DbUtil -> DB
     * @return
     * @throws Exception
     */
    @RequestMapping("/userlogin")
    public CounterRes login(@RequestParam long uid,
                            @RequestParam String password,
                            @RequestParam String captcha,
                            @RequestParam String captchaId) throws Exception {
        Account account = accountService.login(uid, password, captcha, captchaId);
        if(account == null) {
            return new CounterRes(FAIL, "用户名密码/验证码错误,登录失败", null);
        } else {
            return new CounterRes(account);
        }
    }

    /**
     * 登录失败
     * @return
     */
    @RequestMapping("/loginfail")
    public CounterRes loginFail() {
        return new CounterRes(RELOGIN, "请重新登录", null);
    }

    /**
     * 退出登录
     * @param token
     * @return
     */
    @RequestMapping("/logout")
    public CounterRes logout(@RequestParam String token) {
        accountService.logout(token);
        return new CounterRes(SUCCESS, "退出成功", null);
    }

    /**
     * 修改密码
     */
    @RequestMapping("/updatepwd")
    public CounterRes updatePwd(@RequestParam long uid, @RequestParam String oldPwd, @RequestParam String newPwd) {
        boolean res = accountService.updatePwd(uid, oldPwd, newPwd);
        if(res) {
            return new CounterRes(SUCCESS, "密码更新成功", null);
        } else {
            return new CounterRes(FAIL, "密码更新失败", null);
        }
    }
}
