package com.javawxid.service;

import com.javawxid.bean.UserInfo;

import java.util.List;

public interface UserInfoService {

    List<UserInfo> getUserInfo();

    Integer addUserInfo(UserInfo userinfo);

    Integer deleteUserInfo(Integer id);

    Integer updateUserInfo(UserInfo userinfo);
}
