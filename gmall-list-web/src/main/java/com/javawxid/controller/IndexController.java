package com.javawxid.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.javawxid.bean.BaseCatalog1;
import com.javawxid.bean.UserInfo;
import com.javawxid.service.ListService;
import com.javawxid.service.UserService;
import com.javawxid.util.CookieUtil;
import com.javawxid.util.CreateFileUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
@Controller
public class IndexController {

    @Reference
    ListService listService;

    @Reference
    UserService userService;

    @RequestMapping("index")
    public String index(HttpServletRequest request,ModelMap modelMap){
        //从cookie中获取userID
        String userId = CookieUtil.getCookieValue(request, "userId", true);
        UserInfo userInfo = userService.getUserById(userId);
        if(userInfo != null){
            String nickName = userInfo.getNickName();
            modelMap.put("nickName",nickName);
        }
        return "index";
    }

    @RequestMapping("catalog")
    public String contextLoads() {
        //从库中根据一级分类id查询二级分类关联的三级分类表的数据
        List<BaseCatalog1> baseCatalog1List = listService.catalogJson();
        //将list集合转换成json数组
        JSONArray array= JSONArray.parseArray(JSON.toJSONString(baseCatalog1List));
        //将json数组转换成字符串类型
        String toString = array.toString();
        //使用工具类生成json文件
        CreateFileUtil.createJsonFile(toString, "d:/index/json", "catalog3");
        return "index";
    }
}
