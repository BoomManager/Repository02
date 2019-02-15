package com.javawxid.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.javawxid.bean.UserInfo;
import com.javawxid.service.UserService;
import com.javawxid.util.CookieUtil;
import com.javawxid.util.JwtUtil;
import com.javawxid.util.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {
    @Reference
    UserService userService;

    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request,String requestId,String token, ModelMap map){
        Map userMap = JwtUtil.decode("gmallkey", token, MD5Utils.md5(requestId));
        if(userMap!=null){//{status:success,userId:2}
            return "success";
        }else{
            return "fail";
        }
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response, ModelMap map){
        String token = "";
        // 用户名和密码进行验证
        UserInfo userLogin = userService.login(userInfo);
        if(userLogin==null){
            // 用户名或者密码错误
            return "err";
        }else{
            // 生成token
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put("userId",userLogin.getId());
            String id = userLogin.getId();
            //将userId保存到cookie中
            CookieUtil.setCookie(request,response,"userId",userLogin.getId(),60*60*24*7,true);
            String nip = request.getHeader("request-forwared-for");// nginx中的
            if(StringUtils.isBlank(nip)){
                nip = request.getRemoteAddr();// servlet中ip
                if(StringUtils.isBlank(nip)){
                    nip = "127.0.0.1";
                }
            }
            token = JwtUtil.encode("gmallkey", stringStringHashMap, MD5Utils.md5(nip));
            // 将用户数据放入缓存
            userService.addUserCache(userLogin);
        }
        return token;
    }

    @RequestMapping("index")
    public String index(String returnUrl, ModelMap map){
        map.put("originUrl",returnUrl);
        return "index";
    }

}
