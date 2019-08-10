package com.miyako.subject.service.course.consumer.config;

import com.miyako.subject.dubbo.aop.MethodLog;
import com.miyako.subject.service.redis.key.StudentKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName AuthInterceptor
 * Description //TODO
 * Author weila
 * Date 2019-08-10-0010 14:58
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter{

    private static Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    @MethodLog(value = "AuthInterceptor", operationType = "拦截器", operationName = "preHandle", operationArgs = "请求")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String token = request.getParameter("token");
        logger.info("param token===>"+token);
        if(token == null){
            // 参数中没有token
            String cookieToken = getCookieValue(request,"token");
            logger.info("cookie token===>"+token);
            if(cookieToken == null){
                //同样没有cookie，即未登录
                return false;
            }
        }else{
            // 参数中有token，从登陆服务器过来
            Cookie cookie = new Cookie("token", token);
            // 设置cookie的有效期，与session有效期一致
            cookie.setMaxAge(StudentKey.token.expireSeconds());
            // 设置网站的根目录
            cookie.setPath("/");
            // 需要写到response中
            response.addCookie(cookie);
            logger.info("set cookie token===>"+token);
        }
        return super.preHandle(request, response, handler);
    }

    @MethodLog(value = "AuthInterceptor", operationType = "拦截器", operationName = "getCookieValue", operationArgs = "获取cookie")
    public String getCookieValue(HttpServletRequest request, String cookie1NameToken) {//COOKIE1_NAME_TOKEN-->"token"
        //遍历request里面所有的cookie
        Cookie[] cookies=request.getCookies();
        if(cookies!=null) {
            for(Cookie cookie :cookies) {
                if(cookie.getName().equals(cookie1NameToken)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
