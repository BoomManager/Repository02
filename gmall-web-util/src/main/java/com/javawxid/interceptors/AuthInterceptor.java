package com.javawxid.interceptors;

import com.javawxid.annotations.LoginRequired;
import com.javawxid.util.CookieUtil;
import com.javawxid.util.HttpClientUtil;
import com.javawxid.util.JwtUtil;
import com.javawxid.util.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    //在执行目标handler方法之前执行，如果返回true，则继续执行后续拦截器和目标handler方法；如果返回false则不执行。注意：返回false时最好借助转发或重定向等方式为客户端提供一个响应页面。
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 注解判断
        HandlerMethod hm = (HandlerMethod) handler;
        //获取到那个方法使用了@LoginRequired注解
        LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);

        String token = "";
        String newToken = request.getParameter("newToken");
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);

        if(StringUtils.isNotBlank(oldToken)){
            token = oldToken;
        }

        if(StringUtils.isNotBlank(newToken)){
            token = newToken;
        }

        if (methodAnnotation != null) {
            // 校验
            boolean loginCheck = false;
            if (StringUtils.isNotBlank(token)) {
                String nip = request.getHeader("request-forwared-for");// nginx中的
                if(StringUtils.isBlank(nip)){
                    nip = request.getRemoteAddr();// servlet中ip
                    if(StringUtils.isBlank(nip)){
                        nip = "127.0.0.1";
                    }
                }
                String success = HttpClientUtil.doGet("http://passport.gmall.com:8085/verify?token="+token+"&requestId="+nip);
                if(success!=null&&success.equals("success")){
                    loginCheck = true;// 远程调用认证中心的验证业务
                    // 将新token更新到cookie
                    CookieUtil.setCookie(request,response,"oldToken",token,60*60*24,true);
                    // 添加用户信息到请求的业务中
                    Map userMap = JwtUtil.decode("gmallkey", token, MD5Utils.md5(nip));
                    String userId = (String)userMap.get("userId");
                    request.setAttribute("userId",userId);
                }
            }
            // 校验不通过，并且必须登陆
            if (loginCheck == false && methodAnnotation.isNeedLogin() == true) {
                response.sendRedirect("http://passport.gmall.com:8085/index?returnUrl=" + request.getRequestURL());
                return false;
            }

        }
        return true;

    }
}
