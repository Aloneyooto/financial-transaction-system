package com.alone.counter.filter;

import com.alone.counter.service.AccountService;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.filter
 * @Author: Alone
 * @CreateTime: 2020-10-29 12:57
 * @Description: 处理跨域的过滤器
 */
@Component //不要忘记加这个了
public class SessionCheckFilter implements Filter {

    @Autowired
    private AccountService accountService;

    //白名单
    private Set<String> whiteRootPaths = Sets.newHashSet(
            "login", "msgsocket", "test"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //解决ajax跨域问题
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        response.addHeader("Access-Control-Allow-Origin", "*");
        //进行身份校验
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //http://localhost:8090/login/pwdsetting -> /login/pwdsetting
        String path = request.getRequestURI();
        String[] split = path.split("/");
        if(split.length < 2) {
            //跳转至失败页面
            request.getRequestDispatcher(
                    "/login/loginfail"
            ).forward(servletRequest, servletResponse);
        } else {
            if(!whiteRootPaths.contains(split[1])){
                //不在白名单 验证token
                if(accountService.accountExistInCache(request.getParameter("token"))){
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    request.getRequestDispatcher("/login/loginfail").forward(servletRequest, servletResponse);
                }
            } else {
                //在白名单 放行
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }

    @Override
    public void destroy() {

    }
}
