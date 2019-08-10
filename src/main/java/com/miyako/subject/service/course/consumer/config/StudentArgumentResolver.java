package com.miyako.subject.service.course.consumer.config;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.miyako.subject.commons.domain.TbStudent;
import com.miyako.subject.service.user.api.TbUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName StudentArgumentResolver
 * Description //TODO
 * Author weila
 * Date 2019-08-10-0010 13:46
 */
@Component
public class StudentArgumentResolver implements HandlerMethodArgumentResolver{

    private static Logger log = LoggerFactory.getLogger(StudentArgumentResolver.class);

    @Reference
    private TbUserService userService;

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

    @Override
    public boolean supportsParameter(MethodParameter methodParameter){
        //返回参数的类型
        Class<?> clazz=methodParameter.getParameterType();
        return clazz == TbStudent.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception{
        HttpServletRequest request=nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response=nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        String paramToken=request.getParameter("token");
        log.info("paramToken: "+paramToken);
        //获取cookie
        String cookieToken = getCookieValue(request,"token");
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken))
        {
            return null;
        }
        String token=StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        log.info("token: ===>"+token);
        TbStudent tbStudent = userService.getByToken(token);
        log.info("studnent: ===>" + tbStudent.getName());
        //去取得已经保存的user，因为在用户登录的时候,user已经保存到threadLocal里面了，因为拦截器首先执行，然后才是取得参数
        return tbStudent;
    }
}