package com.javawxid.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.javawxid.bean.UserInfo;
import com.javawxid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserInfoController {
    @Reference
    UserService userService;
    /**
     * 用户列表查询
     * @return
     */
    @ResponseBody
    @RequestMapping("userInfoList")
    public List<UserInfo> getUserInfo(){
        List<UserInfo> userInfoList = userService.getUserList();
        return userInfoList;
    }

    /**
     * 添加用户
     * @param userInfo
     * @return
     */
    @ResponseBody
    @RequestMapping("userInfoAdd")
    public List<UserInfo> addUserInfo(UserInfo userInfo){
//        Integer integer = userInfoService.addUserInfo(userInfo);
//        if(integer<0){
//            System.out.print("添加失败");
//        }
//        List<UserInfo> userInfoList = userInfoService.getUserInfo();
        return null;
    }

    /**
     * 修改用户
     * @param userInfo
     * @return
     */
    @ResponseBody
    @RequestMapping("userInfoUpdate")
    public List<UserInfo> updateUserInfo(UserInfo userInfo){
//        Integer integer = userInfoService.updateUserInfo(userInfo);
//        List<UserInfo> userInfoList = userInfoService.getUserInfo();
        return null;
    }

    /**
     * 通过id删除用户
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("userInfoDelete")
    public List<UserInfo> deleteUserInfo(Integer id){
//        Integer integer = userInfoService.deleteUserInfo(id);
//        if(integer<0){
//            System.out.print("删除失败！");
//        }
//        List<UserInfo> userInfoList = userInfoService.getUserInfo();
        return null;

    }
}
