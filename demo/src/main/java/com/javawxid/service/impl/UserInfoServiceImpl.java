package com.javawxid.service.impl;

import com.javawxid.bean.UserInfo;
import com.javawxid.mapper.UserInfoMapper;
import com.javawxid.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Override
    public List<UserInfo> getUserInfo() {
        return userInfoMapper.selectAll();
    }

    @Override
    public Integer addUserInfo(UserInfo userinfo) {
        return userInfoMapper.insert(userinfo);
    }

    @Override
    public Integer deleteUserInfo(Integer id) {
        return userInfoMapper.deleteById(id);
    }

    @Override
    public Integer updateUserInfo(UserInfo userinfo) {
        return userInfoMapper.updateById(userinfo);
    }
}
